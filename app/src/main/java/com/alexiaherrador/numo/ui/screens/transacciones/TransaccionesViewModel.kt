package com.alexiaherrador.numo.ui.screens.transacciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.domain.model.Gasto
import com.alexiaherrador.numo.domain.model.toEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransaccionesViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    private val _todosLosGastos = MutableStateFlow<List<Gasto>>(emptyList())

    private val _filtroCategoria = MutableStateFlow<String?>(null)
    val filtroCategoria: StateFlow<String?> = _filtroCategoria.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val gastosFiltrados: StateFlow<List<Gasto>> = combine(
        _todosLosGastos,
        _filtroCategoria,
        _busqueda
    ) { gastos, filtro, busqueda ->
        gastos
            .filter { gasto ->
                filtro == null || gasto.categoria.name == filtro
            }
            .filter { gasto ->
                busqueda.isBlank() ||
                        gasto.descripcion.contains(busqueda, ignoreCase = true) ||
                        gasto.categoria.nombreMostrar.contains(busqueda, ignoreCase = true)
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        cargarGastos()
    }

    private fun cargarGastos() {
        viewModelScope.launch {
            repository.obtenerTodosLosGastos().collect {
                _todosLosGastos.value = it
            }
        }
    }

    fun eliminarGasto(gasto: Gasto) {
        viewModelScope.launch {
            repository.eliminarGasto(gasto.toEntity())
        }
    }

    fun onFiltroCategoria(categoria: String?) {
        _filtroCategoria.value = categoria
    }

    fun onBusquedaCambiada(texto: String) {
        _busqueda.value = texto
    }
}

class TransaccionesViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransaccionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransaccionesViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}