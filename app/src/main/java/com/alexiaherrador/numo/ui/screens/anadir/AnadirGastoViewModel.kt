package com.alexiaherrador.numo.ui.screens.anadir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alexiaherrador.numo.data.local.entity.GastoEntity
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.domain.model.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnadirGastoViewModel(
    private val repository: GastosRepository
) : ViewModel() {

    private val _cantidad = MutableStateFlow("")
    val cantidad: StateFlow<String> = _cantidad.asStateFlow()

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow<Categoria?>(null)
    val categoriaSeleccionada: StateFlow<Categoria?> = _categoriaSeleccionada.asStateFlow()

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso.asStateFlow()

    fun onCantidadCambiada(valor: String) {
        // Solo permite números y un punto decimal
        if (valor.isEmpty() || valor.matches(Regex("^\\d*\\.?\\d*$"))) {
            _cantidad.value = valor
        }
    }

    fun onDescripcionCambiada(texto: String) {
        _descripcion.value = texto
    }

    fun onCategoriaSeleccionada(categoria: Categoria) {
        _categoriaSeleccionada.value = categoria
    }

    fun guardarGasto() {
        val cantidadDouble = _cantidad.value.toDoubleOrNull() ?: return
        val categoria = _categoriaSeleccionada.value ?: return

        viewModelScope.launch {
            repository.guardarGasto(
                GastoEntity(
                    cantidad = cantidadDouble,
                    descripcion = _descripcion.value.ifBlank { categoria.nombreMostrar },
                    categoria = categoria.name,
                    fecha = System.currentTimeMillis(),
                    notaOpcional = null
                )
            )
            _guardadoExitoso.value = true
        }
    }
}

class AnadirGastoViewModelFactory(
    private val repository: GastosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnadirGastoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnadirGastoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}