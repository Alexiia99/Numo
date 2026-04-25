package com.alexiaherrador.numo.ui.screens.ajustes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.local.UserPreferences
import com.alexiaherrador.numo.domain.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.alexiaherrador.numo.data.repository.GastosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.alexiaherrador.numo.R
import com.alexiaherrador.numo.domain.model.toEntity
import com.alexiaherrador.numo.notifications.NotificacionWorker
import com.alexiaherrador.numo.ui.theme.NumoColors

class AjustesViewModel(
    private val userPreferences: UserPreferences,
    private val repository: GastosRepository
) : ViewModel() {

    val usuario: StateFlow<Usuario?> = userPreferences.usuario.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso.asStateFlow()

    // ← Fix: pasamos hora y minutos directamente al worker
    fun guardarCambios(usuario: Usuario, context: Context? = null) {
        viewModelScope.launch {
            userPreferences.guardarUsuario(usuario)
            _guardadoExitoso.value = true
            context?.let {
                NotificacionWorker.programar(
                    it,
                    usuario.horaNotificacion,
                    usuario.minutosNotificacion
                )
            }
        }
    }

    fun borrarTodo() {
        viewModelScope.launch {
            userPreferences.borrarTodo()
            repository.obtenerTodosLosGastos().first().forEach {
                repository.eliminarGasto(it.toEntity())
            }
            repository.borrarTodosLosPresupuestos()
            repository.obtenerRecurrentes().first().forEach {
                repository.eliminarRecurrente(it)
            }
        }
    }

    fun exportarCSV(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gastos = repository.obtenerTodosLosGastos().first()
                val archivo = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "numo_gastos_${System.currentTimeMillis()}.csv"
                )

                archivo.bufferedWriter().use { writer ->
                    writer.write("Fecha,Descripción,Categoría,Cantidad\n")
                    gastos.forEach { gasto ->
                        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale("es"))
                            .format(Date(gasto.fecha))
                        writer.write("$fecha,${gasto.descripcion},${gasto.categoria.nombreMostrar},${gasto.cantidad}\n")
                    }
                }

                withContext(Dispatchers.Main) {
                    lanzarNotificacionDescarga(context, archivo, "CSV")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun exportarPDF(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val gastos = repository.obtenerTodosLosGastos().first()
                val documento = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
                val pagina = documento.startPage(pageInfo)
                val canvas = pagina.canvas
                val paint = android.graphics.Paint().apply {
                    textSize = 14f
                    color = android.graphics.Color.BLACK
                }

                paint.textSize = 20f
                paint.isFakeBoldText = true
                canvas.drawText("Numo — Informe de gastos", 40f, 60f, paint)

                paint.textSize = 11f
                paint.isFakeBoldText = false
                paint.color = android.graphics.Color.GRAY
                canvas.drawText(
                    "Generado el ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es")).format(Date())}",
                    40f, 82f, paint
                )

                paint.color = android.graphics.Color.BLACK
                paint.textSize = 12f
                paint.isFakeBoldText = true
                var y = 120f
                canvas.drawText("Fecha", 40f, y, paint)
                canvas.drawText("Descripción", 130f, y, paint)
                canvas.drawText("Categoría", 310f, y, paint)
                canvas.drawText("Cantidad", 460f, y, paint)

                y += 8f
                paint.strokeWidth = 1f
                canvas.drawLine(40f, y, 555f, y, paint)
                y += 16f

                paint.isFakeBoldText = false
                paint.textSize = 11f
                var totalGeneral = 0.0

                gastos.forEach { gasto ->
                    if (y > 800f) {
                        documento.finishPage(pagina)
                        documento.startPage(
                            PdfDocument.PageInfo.Builder(595, 842, documento.pages.size + 1).create()
                        )
                        y = 40f
                    }
                    val fecha = SimpleDateFormat("dd/MM/yy", Locale("es")).format(Date(gasto.fecha))
                    val desc = if (gasto.descripcion.length > 22) gasto.descripcion.take(22) + "…" else gasto.descripcion
                    canvas.drawText(fecha, 40f, y, paint)
                    canvas.drawText(desc, 130f, y, paint)
                    canvas.drawText(gasto.categoria.nombreMostrar, 310f, y, paint)
                    canvas.drawText("${String.format("%.2f", gasto.cantidad)} ${NumoColors.moneda}", 460f, y, paint)
                    totalGeneral += gasto.cantidad
                    y += 18f
                }

                y += 8f
                paint.isFakeBoldText = true
                canvas.drawLine(40f, y, 555f, y, paint)
                y += 16f
                canvas.drawText("TOTAL", 310f, y, paint)
                canvas.drawText("${String.format("%.2f", totalGeneral)} ${NumoColors.moneda}", 460f, y, paint)

                documento.finishPage(pagina)

                val archivo = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "numo_informe_${System.currentTimeMillis()}.pdf"
                )
                documento.writeTo(archivo.outputStream())
                documento.close()

                withContext(Dispatchers.Main) {
                    lanzarNotificacionDescarga(context, archivo, "PDF")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "❌ Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun lanzarNotificacionDescarga(context: Context, archivo: File, tipo: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )

        val mimeType = if (tipo == "CSV") "text/csv" else "application/pdf"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, "numo_descargas")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("✅ $tipo exportado")
            .setContentText("Toca para abrir el archivo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        if (manager.areNotificationsEnabled()) {
            manager.notify(System.currentTimeMillis().toInt(), notificacion)
        }
    }

    fun resetGuardado() {
        _guardadoExitoso.value = false
    }
}

class AjustesViewModelFactory(
    private val userPreferences: UserPreferences,
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AjustesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AjustesViewModel(userPreferences, repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}