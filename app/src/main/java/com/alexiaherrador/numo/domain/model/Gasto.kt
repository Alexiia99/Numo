package com.alexiaherrador.numo.domain.model

data class Gasto(
    val id : Int,
    val cantidad : Double,
    val descripcion : String,
    val categoria : Categoria,
    val fecha : Long,
    val notaOpcional : String?
)