package com.alexiaherrador.numo.domain.model

enum class Categoria(
    val nombreMostrar: String,
    val emoji: String
) {
    ALIMENTACION("Alimentación", "🛒"),
    HOGAR("Hogar", "🏠"),
    RESTAURANTE("Restaurante", "🍽️"),
    TRANSPORTE("Transporte", "⛽"),
    SALUD("Salud", "💊"),
    OCIO("Ocio", "🎬"),
    ROPA("Ropa", "👕"),
    OTROS("Otros", "📦")
}