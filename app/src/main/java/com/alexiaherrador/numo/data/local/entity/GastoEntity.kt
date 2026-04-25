package com.alexiaherrador.numo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cantidad: Double,
    val descripcion: String,
    val categoria: String,
    val fecha: Long,
    val notaOpcional: String?
)