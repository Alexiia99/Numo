package com.alexiaherrador.numo.ui.screens.recurrentes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.local.entity.GastoEntity
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.domain.model.GastoRecurrente
import com.alexiaherrador.numo.domain.model.Periodicidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class RecurrentesViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    val recurrentes: StateFlow<List<GastoRecurrente>> = repository
        .obtenerRecurrentes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pendientes = MutableStateFlow<List<GastoRecurrente>>(emptyList())
    val pendientes: StateFlow<List<GastoRecurrente>> = _pendientes.asStateFlow()

    init {
        cargarPendientes()
    }

    private fun cargarPendientes() {
        viewModelScope.launch {
            val ahora = System.currentTimeMillis()
            val pendientesNuevos = repository.obtenerRecurrentesPendientes()
                .filter { gasto ->
                    val diasRestantes = (gasto.proximoPago - ahora) / (1000 * 60 * 60 * 24)
                    diasRestantes <= gasto.diasAviso
                }
            _pendientes.value = pendientesNuevos
        }
    }

    fun confirmarRecurrente(gasto: GastoRecurrente) {
        viewModelScope.launch {
            // 1. Registra como gasto normal con fecha de hoy
            repository.guardarGasto(
                GastoEntity(
                    cantidad = gasto.cantidad,
                    descripcion = gasto.nombre,
                    categoria = gasto.categoria.name,
                    fecha = System.currentTimeMillis(),
                    notaOpcional = "Recurrente · ${gasto.periodicidad.label}"
                )
            )
            // 2. Actualiza el próximo pago
            repository.actualizarRecurrente(
                gasto.copy(proximoPago = calcularProximoPago(gasto.proximoPago, gasto.periodicidad))
            )
            cargarPendientes()
        }
    }

    fun ignorarRecurrente(gasto: GastoRecurrente) {
        viewModelScope.launch {
            repository.actualizarRecurrente(
                gasto.copy(proximoPago = calcularProximoPago(gasto.proximoPago, gasto.periodicidad))
            )
            cargarPendientes()
        }
    }

    fun guardarRecurrente(gasto: GastoRecurrente) {
        viewModelScope.launch {
            // 1. Guarda el recurrente
            repository.guardarRecurrente(gasto)

            // 2. Registra el gasto HOY en el mes actual
            repository.guardarGasto(
                GastoEntity(
                    cantidad = gasto.cantidad,
                    descripcion = gasto.nombre,
                    categoria = gasto.categoria.name,
                    fecha = System.currentTimeMillis(),  // ← siempre hoy
                    notaOpcional = "Recurrente · ${gasto.periodicidad.label}"
                )
            )

            cargarPendientes()
        }
    }

    fun eliminarRecurrente(gasto: GastoRecurrente) {
        viewModelScope.launch {
            repository.eliminarRecurrente(gasto)
            cargarPendientes()
        }
    }

    private fun calcularProximoPago(actual: Long, periodicidad: Periodicidad): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = actual
        when (periodicidad) {
            Periodicidad.MENSUAL -> cal.add(Calendar.MONTH, 1)
            Periodicidad.TRIMESTRAL -> cal.add(Calendar.MONTH, 3)
            Periodicidad.ANUAL -> cal.add(Calendar.YEAR, 1)
        }
        return cal.timeInMillis
    }
}

class RecurrentesViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecurrentesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecurrentesViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}