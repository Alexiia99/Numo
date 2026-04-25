package com.alexiaherrador.numo.ui.navigation

sealed class Rutas(val ruta: String) {
    object Dashboard : Rutas("dashboard")
    object Transacciones : Rutas("transacciones")
    object AnadirGasto : Rutas("anadir_gasto")
    object Presupuestos : Rutas("presupuestos")
    object Estadisticas : Rutas("estadisticas")
    object Ajustes : Rutas("ajustes")
    object Recurrentes : Rutas("recurrentes")
}