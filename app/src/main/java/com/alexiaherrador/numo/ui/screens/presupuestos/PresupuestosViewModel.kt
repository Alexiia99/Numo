package com.alexiaherrador.numo.ui.screens.presupuestos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.local.entity.PresupuestoEntity
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.domain.model.Gasto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
class PresupuestosViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    val presupuestos: StateFlow<List<PresupuestoEntity>> =
        repository.obtenerPresupuestos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _gastosMes = MutableStateFlow<List<Gasto>>(emptyList())

    val gastosPorCategoria: StateFlow<Map<String, Double>> = _gastosMes
        .map { gastos ->
            gastos.groupBy { it.categoria.name }
                .mapValues { entrada -> entrada.value.sumOf { it.cantidad } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        cargarGastosMes()
    }

    private fun cargarGastosMes() {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)
        viewModelScope.launch {
            repository.obtenerGastosMes(mes, anio).collect {
                _gastosMes.value = it
            }
        }
    }

    fun guardarPresupuesto(categoria: String, tope: Double) {
        viewModelScope.launch {
            repository.guardarPresupuesto(
                PresupuestoEntity(
                    categoria = categoria,
                    tope = tope
                )
            )
        }
    }

    fun eliminarPresupuesto(presupuesto: PresupuestoEntity) {
        viewModelScope.launch {
            repository.eliminarPresupuesto(presupuesto)
        }
    }
}

class PresupuestosViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PresupuestosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PresupuestosViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}