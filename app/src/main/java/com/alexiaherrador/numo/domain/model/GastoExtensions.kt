package com.alexiaherrador.numo.domain.model

import com.alexiaherrador.numo.data.local.entity.GastoEntity

fun Gasto.toEntity(): GastoEntity = GastoEntity(
    id = this.id,
    cantidad = this.cantidad,
    descripcion = this.descripcion,
    categoria = this.categoria.name,
    fecha = this.fecha,
    notaOpcional = this.notaOpcional
)