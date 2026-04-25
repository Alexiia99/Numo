package com.alexiaherrador.numo.domain.model

import com.alexiaherrador.numo.data.local.entity.GastoRecurrenteEntity

fun GastoRecurrenteEntity.toDomain(): GastoRecurrente {
    return GastoRecurrente(
        id = this.id,
        nombre = this.nombre,
        cantidad = this.cantidad,
        categoria = Categoria.valueOf(this.categoria),
        periodicidad = Periodicidad.valueOf(this.periodicidad),
        proximoPago = this.proximoPago,
        diasAviso = this.diasAviso,
        activo = this.activo
    )
}

fun GastoRecurrente.toEntity(): GastoRecurrenteEntity {
    return GastoRecurrenteEntity(
        id = this.id,
        nombre = this.nombre,
        cantidad = this.cantidad,
        categoria = this.categoria.name,
        periodicidad = this.periodicidad.name,
        proximoPago = this.proximoPago,
        diasAviso = this.diasAviso,
        activo = this.activo
    )
}

