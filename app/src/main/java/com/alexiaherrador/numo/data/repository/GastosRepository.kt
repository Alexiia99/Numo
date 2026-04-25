package com.alexiaherrador.numo.data.repository

import com.alexiaherrador.numo.data.local.dao.GastoDao
import com.alexiaherrador.numo.data.local.dao.GastoRecurrenteDao
import com.alexiaherrador.numo.data.local.dao.PresupuestoDao
import com.alexiaherrador.numo.data.local.entity.GastoEntity
import com.alexiaherrador.numo.data.local.entity.GastoRecurrenteEntity
import com.alexiaherrador.numo.data.local.entity.PresupuestoEntity
import com.alexiaherrador.numo.domain.model.Categoria
import com.alexiaherrador.numo.domain.model.Gasto
import com.alexiaherrador.numo.domain.model.GastoRecurrente
import com.alexiaherrador.numo.domain.model.Periodicidad
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class GastosRepository(
    private val gastoDao: GastoDao,
    private val presupuestoDao: PresupuestoDao,
    private val gastoRecurrenteDao: GastoRecurrenteDao
) {
    // — Gastos —
    fun obtenerTodosLosGastos(): Flow<List<Gasto>> =
        gastoDao.obtenerTodos().map { lista ->
            lista.map { it.toDomain() }
        }
    fun obtenerGastosMes(mes: Int, anio: Int, diaInicio: Int = 1): Flow<List<Gasto>> {
        val (inicio, fin) = calcularRangoMes(mes, anio, diaInicio)
        return gastoDao.obtenerPorMes(inicio, fin).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    suspend fun totalGastadoEnMes(mes: Int, anio: Int, diaInicio: Int = 1): Double {
        val (inicio, fin) = calcularRangoMes(mes, anio, diaInicio)
        return gastoDao.totalGastadoEnMes(inicio, fin) ?: 0.0
    }

    suspend fun guardarGasto(gasto: GastoEntity) =
        gastoDao.insertar(gasto)

    suspend fun actualizarGasto(gasto: GastoEntity) =
        gastoDao.actualizar(gasto)

    suspend fun eliminarGasto(gasto: GastoEntity) =
        gastoDao.eliminar(gasto)

    suspend fun totalGastadoEnMes(mes: Int, anio: Int): Double {
        val (inicio, fin) = calcularRangoMes(mes, anio)
        return gastoDao.totalGastadoEnMes(inicio, fin) ?: 0.0
    }

    // — Gastos recurrentes —
    fun obtenerRecurrentes(): Flow<List<GastoRecurrente>> =
        gastoRecurrenteDao.obtenerTodos().map { lista ->
            lista.map { it.toDomain() }
        }

    suspend fun guardarRecurrente(gasto: GastoRecurrente) =
        gastoRecurrenteDao.insertar(gasto.toEntity())

    suspend fun actualizarRecurrente(gasto: GastoRecurrente) =
        gastoRecurrenteDao.actualizar(gasto.toEntity())

    suspend fun eliminarRecurrente(gasto: GastoRecurrente) =
        gastoRecurrenteDao.eliminar(gasto.toEntity())

    suspend fun obtenerRecurrentesPendientes(): List<GastoRecurrente> {
        val hasta = System.currentTimeMillis()
        return gastoRecurrenteDao.obtenerPendientes(hasta)
            .map { it.toDomain() }
    }

    // — Presupuestos —
    fun obtenerPresupuestos(): Flow<List<PresupuestoEntity>> =
        presupuestoDao.obtenerTodos()

    suspend fun guardarPresupuesto(presupuesto: PresupuestoEntity) =
        presupuestoDao.insertar(presupuesto)

    suspend fun obtenerTopePorCategoria(categoria: String): Double =
        presupuestoDao.obtenerTopePorCategoria(categoria) ?: 0.0

    suspend fun eliminarPresupuesto(presupuesto: PresupuestoEntity) =
        presupuestoDao.eliminar(presupuesto)


    // — Ahorro —
    suspend fun calcularAhorroAcumulado(): Double {
        val totalGastado = gastoDao.obtenerTodos().first().sumOf { it.cantidad }
        val topes = presupuestoDao.obtenerTodos().first()
        val totalPresupuestado = topes.sumOf { it.tope }
        return (totalPresupuestado - totalGastado).coerceAtLeast(0.0)
    }

    // — Utilidad —
    private fun calcularRangoMes(mes: Int, anio: Int, diaInicio: Int = 1): Pair<Long, Long> {
        val calendario = Calendar.getInstance()

        // Inicio: día de corte del mes actual
        calendario.set(anio, mes - 1, diaInicio, 0, 0, 0)
        calendario.set(Calendar.MILLISECOND, 0)
        val inicio = calendario.timeInMillis

        // Fin: día anterior al de corte del mes siguiente
        calendario.set(anio, mes, diaInicio, 0, 0, 0) // mes siguiente
        calendario.add(Calendar.DAY_OF_MONTH, -1)
        calendario.set(Calendar.HOUR_OF_DAY, 23)
        calendario.set(Calendar.MINUTE, 59)
        calendario.set(Calendar.SECOND, 59)
        calendario.set(Calendar.MILLISECOND, 999)
        val fin = calendario.timeInMillis

        return Pair(inicio, fin)
    }

    private fun GastoEntity.toDomain(): Gasto {
        return Gasto(
            id = this.id,
            cantidad = this.cantidad,
            descripcion = this.descripcion,
            categoria = Categoria.valueOf(this.categoria),
            fecha = this.fecha,
            notaOpcional = this.notaOpcional
        )
    }
    private fun Gasto.toEntity(): GastoEntity {
        return GastoEntity(
            id = this.id,
            cantidad = this.cantidad,
            descripcion = this.descripcion,
            categoria = this.categoria.name,
            fecha = this.fecha,
            notaOpcional = this.notaOpcional
        )
    }

    suspend fun borrarTodosLosPresupuestos() {
        presupuestoDao.borrarTodos()
    }

    private fun GastoRecurrenteEntity.toDomain(): GastoRecurrente {
        return GastoRecurrente(
            id = this.id,
            nombre = this.nombre,
            cantidad = this.cantidad,
            categoria = Categoria.valueOf(this.categoria),
            periodicidad = Periodicidad.valueOf(this.periodicidad),
            proximoPago = this.proximoPago,
            diasAviso = this.diasAviso,
            activo = this.activo
        )
    }

    private fun GastoRecurrente.toEntity(): GastoRecurrenteEntity {
        return GastoRecurrenteEntity(
            id = this.id,
            nombre = this.nombre,
            cantidad = this.cantidad,
            categoria = this.categoria.name,
            periodicidad = this.periodicidad.name,
            proximoPago = this.proximoPago,
            diasAviso = this.diasAviso,
            activo = this.activo
        )
    }

}