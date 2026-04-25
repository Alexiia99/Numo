package com.alexiaherrador.numo.domain.model

data class GastoRecurrente(
    val id: Int = 0,
    val nombre: String,
    val cantidad: Double,
    val categoria: Categoria,
    val periodicidad: Periodicidad,
    val proximoPago: Long,
    val diasAviso: Int = 1,
    val activo: Boolean = true
)