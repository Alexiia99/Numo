package com.alexiaherrador.numo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presupuestos")
data class PresupuestoEntity(
    @PrimaryKey val categoria: String,
    val tope: Double
)