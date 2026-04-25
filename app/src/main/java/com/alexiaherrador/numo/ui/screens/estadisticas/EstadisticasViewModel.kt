package com.alexiaherrador.numo.ui.screens.estadisticas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.domain.model.Categoria
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EstadisticasViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    private val _gastosPorCategoria = MutableStateFlow<Map<Categoria, Double>>(emptyMap())
    val gastosPorCategoria: StateFlow<Map<Categoria, Double>> = _gastosPorCategoria.asStateFlow()

    private val _evolucionMensual = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val evolucionMensual: StateFlow<List<Pair<String, Double>>> = _evolucionMensual.asStateFlow()

    private val _ahorroAcumulado = MutableStateFlow(0.0)
    val ahorroAcumulado: StateFlow<Double> = _ahorroAcumulado.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        val calendario = Calendar.getInstance()
        val mes = calendario.get(Calendar.MONTH) + 1
        val anio = calendario.get(Calendar.YEAR)

        // Gastos por categoría del mes actual
        viewModelScope.launch {
            repository.obtenerGastosMes(mes, anio).collect { gastos ->
                _gastosPorCategoria.value = gastos
                    .groupBy { it.categoria }
                    .mapValues { entrada -> entrada.value.sumOf { it.cantidad } }
            }
        }

        // Evolución últimos 6 meses
        viewModelScope.launch {
            val evolucion = mutableListOf<Pair<String, Double>>()
            val cal = Calendar.getInstance()

            repeat(6) {
                val m = cal.get(Calendar.MONTH) + 1
                val a = cal.get(Calendar.YEAR)
                val total = repository.totalGastadoEnMes(m, a)
                val etiqueta = SimpleDateFormat("MMM", Locale("es")).format(cal.time)
                    .replaceFirstChar { it.uppercase() }
                evolucion.add(0, Pair(etiqueta, total))
                cal.add(Calendar.MONTH, -1)
            }

            _evolucionMensual.value = evolucion
        }

        // Ahorro acumulado
        viewModelScope.launch {
            _ahorroAcumulado.value = repository.calcularAhorroAcumulado()
        }
    }
}

class EstadisticasViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstadisticasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstadisticasViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}