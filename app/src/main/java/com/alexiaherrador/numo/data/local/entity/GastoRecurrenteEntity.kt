package com.alexiaherrador.numo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos_recurrentes")
data class GastoRecurrenteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val cantidad: Double,
    val categoria: String,
    val periodicidad: String,
    val proximoPago: Long,
    val diasAviso: Int = 1,
    val activo: Boolean = true
)