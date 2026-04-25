package com.alexiaherrador.numo.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.alexiaherrador.numo.R
import com.alexiaherrador.numo.data.local.AppDatabase
import com.alexiaherrador.numo.data.local.UserPreferences
import com.alexiaherrador.numo.data.repository.GastosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class NotificacionWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Log.d("Numo", "Worker ejecutado a las ${Date()}")
        return try {
            if (!hayGastosHoy()) lanzarNotificacion()
            comprobarRecurrentes()
            comprobarPresupuesto()
            reprogramarParaMañana()
            Result.success()
        } catch (e: Exception) {
            Log.e("Numo", "Error en worker: ${e.message}")
            Result.retry()
        }
    }

    private suspend fun reprogramarParaMañana() {
        val prefs = UserPreferences(applicationContext)
        val usuario = prefs.usuario.first()
        val hora = usuario?.horaNotificacion ?: 21
        val minutos = usuario?.minutosNotificacion ?: 30

        val objetivo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minutos)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val delay = objetivo.timeInMillis - System.currentTimeMillis()
        Log.d("Numo", "Próxima notificación en ${delay / 1000 / 60} minutos a las $hora:${String.format("%02d", minutos)}")
        programarConDelay(applicationContext, delay)
    }

    private fun abrirAppIntent(): PendingIntent {
        val intent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        return PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private suspend fun hayGastosHoy(): Boolean = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(applicationContext)
        val inicioDia = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        db.gastoDao().gastosDeHoy(inicioDia).isNotEmpty()
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Recordatorio diario",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorio para registrar gastos del día"
            }
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    private fun lanzarNotificacion() {
        crearCanalNotificacion()
        val notificacion = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Eh tú 👀")
            .setContentText("¿Se te ha olvidado meter algún gasto hoy?")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(abrirAppIntent())
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(applicationContext)
        if (manager.areNotificationsEnabled()) {
            manager.notify(NOTIFICACION_ID, notificacion)
        }
    }

    private suspend fun comprobarRecurrentes() = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = GastosRepository(
            db.gastoDao(),
            db.presupuestoDao(),
            db.gastoRecurrenteDao()
        )
        val pendientes = repository.obtenerRecurrentesPendientes()

        pendientes.forEach { gasto ->
            val notificacion = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("💳 ${gasto.nombre} — toca pagar")
                .setContentText(
                    "${String.format("%.2f", gasto.cantidad)} · " +
                            "${gasto.periodicidad.label}. ¿Lo confirmas en la app?"
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(abrirAppIntent())
                .setAutoCancel(true)
                .build()

            val manager = NotificationManagerCompat.from(applicationContext)
            if (manager.areNotificationsEnabled()) {
                manager.notify(gasto.id + 100, notificacion)
            }
        }
    }

    private suspend fun comprobarPresupuesto() = withContext(Dispatchers.IO) {
        val db = AppDatabase.getInstance(applicationContext)
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)

        val inicioMes = Calendar.getInstance().apply {
            set(anio, mes - 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val finMes = Calendar.getInstance().apply {
            set(anio, mes - 1, 1, 23, 59, 59)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }.timeInMillis

        // ← Fix: obtenerTodos() en lugar de obtenerPorMes(mes, anio)
        val presupuestos = db.presupuestoDao().obtenerTodos().first()
        val gastos = db.gastoDao().obtenerPorMes(inicioMes, finMes).first()

        val gastosPorCategoria = gastos.groupBy { it.categoria }
            .mapValues { e -> e.value.sumOf { it.cantidad } }

        presupuestos.forEach { presupuesto ->
            val gastado = gastosPorCategoria[presupuesto.categoria] ?: 0.0
            val porcentaje = if (presupuesto.tope > 0) gastado / presupuesto.tope else 0.0

            if (porcentaje >= 0.9) {
                val notificacion = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Tío, estás a dieta 🥗")
                    .setContentText(
                        "${presupuesto.categoria} al ${(porcentaje * 100).toInt()}% " +
                                "del presupuesto. ¡Para de gastar!"
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(abrirAppIntent())
                    .setAutoCancel(true)
                    .build()

                val manager = NotificationManagerCompat.from(applicationContext)
                if (manager.areNotificationsEnabled()) {
                    manager.notify((System.currentTimeMillis() % 1000).toInt(), notificacion)
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "numo_gastos_diarios"
        const val NOTIFICACION_ID = 1

        // ← Fix: recibe hora y minutos directamente, sin releer preferencias
        fun programar(context: Context, hora: Int, minutos: Int) {
            val delay = calcularDelay(hora, minutos)
            Log.d("Numo", "Notificación programada en ${delay / 1000 / 60} minutos a las $hora:${String.format("%02d", minutos)}")
            programarConDelay(context, delay)
        }

        private fun programarConDelay(context: Context, delay: Long) {
            WorkManager.getInstance(context).cancelUniqueWork("numo_notificacion_diaria")

            val request = OneTimeWorkRequestBuilder<NotificacionWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "numo_notificacion_diaria",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }

        private fun calcularDelay(hora: Int, minutos: Int): Long {
            val ahora = Calendar.getInstance()
            val objetivo = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hora)
                set(Calendar.MINUTE, minutos)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(ahora)) add(Calendar.DAY_OF_MONTH, 1)
            }
            return objetivo.timeInMillis - ahora.timeInMillis
        }
    }
}