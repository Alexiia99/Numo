package com.alexiaherrador.numo.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.local.UserPreferences
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.domain.model.Gasto
import com.alexiaherrador.numo.domain.model.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val repository: GastosRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _gastosMes = MutableStateFlow<List<Gasto>>(emptyList())
    val gastosMes: StateFlow<List<Gasto>> = _gastosMes.asStateFlow()

    private val _totalGastado = MutableStateFlow(0.0)
    val totalGastado: StateFlow<Double> = _totalGastado.asStateFlow()

    private val _ahorroAcumulado = MutableStateFlow(0.0)
    val ahorroAcumulado: StateFlow<Double> = _ahorroAcumulado.asStateFlow()

    private val _alertas = MutableStateFlow<List<String>>(emptyList())
    val alertas: StateFlow<List<String>> = _alertas.asStateFlow()

    private val _nombreUsuario = MutableStateFlow("Tú")
    val nombreUsuario: StateFlow<String> = _nombreUsuario.asStateFlow()

    private val _avatarUsuario = MutableStateFlow("👤")
    val avatarUsuario: StateFlow<String> = _avatarUsuario.asStateFlow()

    private val _fotoUri = MutableStateFlow<String?>(null)
    val fotoUri: StateFlow<String?> = _fotoUri.asStateFlow()

    val saldoRestante: StateFlow<Double?> = combine(
        userPreferences.usuario,
        gastosMes
    ) { usuario, gastos ->
        val presupuesto = usuario?.presupuestoGlobal ?: 0.0
        if (presupuesto <= 0) null  // null = no mostrar si no hay presupuesto configurado
        else presupuesto - gastos.sumOf { it.cantidad }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        cargarDatosMes()
        calcularAhorro()
        cargarUsuario()
    }

    private fun cargarUsuario() {
        viewModelScope.launch {
            userPreferences.usuario.collect { usuario ->
                _nombreUsuario.value = usuario?.nombre ?: "Tú"
                _avatarUsuario.value = usuario?.avatarEmoji ?: "👤"
                _fotoUri.value = usuario?.fotoUri
            }
        }
    }

    private fun cargarDatosMes() {
        viewModelScope.launch {
            userPreferences.usuario.collect { usuario ->
                val diaInicio = usuario?.diaIniciaMes ?: 1
                val calendario = Calendar.getInstance()

                // Si hoy es antes del día de corte, estamos en el "mes anterior"
                val hoy = calendario.get(Calendar.DAY_OF_MONTH)
                if (hoy < diaInicio) {
                    calendario.add(Calendar.MONTH, -1)
                }

                val mes = calendario.get(Calendar.MONTH) + 1
                val anio = calendario.get(Calendar.YEAR)

                repository.obtenerGastosMes(mes, anio, diaInicio).collect { gastos ->
                    _gastosMes.value = gastos
                    _totalGastado.value = gastos.sumOf { it.cantidad }
                    comprobarTopes(gastos)
                }
            }
        }
    }

    private fun calcularAhorro() {
        viewModelScope.launch {
            _ahorroAcumulado.value = repository.calcularAhorroAcumulado()
        }
    }

    private fun comprobarTopes(gastos: List<Gasto>) {
        viewModelScope.launch {
            val alertasNuevas = mutableListOf<String>()

            val porCategoria = gastos
                .groupBy { it.categoria }
                .mapValues { entrada -> entrada.value.sumOf { it.cantidad } }

            porCategoria.forEach { (categoria, gastado) ->
                val tope = repository.obtenerTopePorCategoria(categoria.name)
                if (tope > 0 && gastado / tope >= 0.8) {
                    val porcentaje = ((gastado / tope) * 100).toInt()
                    alertasNuevas.add("${categoria.emoji} ${categoria.nombreMostrar} al $porcentaje% del tope")
                }
            }
            _alertas.value = alertasNuevas
        }
    }

    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            repository.eliminarGasto(gasto.toEntity())
        }
    }
}

