package com.alexiaherrador.numo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.ui.navigation.Rutas
import com.alexiaherrador.numo.ui.theme.CremaSuave
import com.alexiaherrador.numo.ui.theme.Jost
import com.alexiaherrador.numo.ui.theme.NumoColors


data class NavItem(
    val etiqueta: String,
    val ruta: String,
    val icono: ImageVector
)

val navItems = listOf(
    NavItem("Inicio", Rutas.Dashboard.ruta, Icons.Rounded.Home),
    NavItem("Gastos", Rutas.Transacciones.ruta, Icons.Rounded.List),
    NavItem("Recurrente", Rutas.Recurrentes.ruta, Icons.Rounded.DateRange),
    NavItem("Topes", Rutas.Presupuestos.ruta, Icons.Rounded.Edit),
    NavItem("Gráficas", Rutas.Estadisticas.ruta, Icons.Rounded.Star),
    NavItem("Ajustes", Rutas.Ajustes.ruta, Icons.Rounded.Settings)
)

@Composable
fun BottomNavBar(
    rutaActual: String?,
    onNavegar: (String) -> Unit
) {
    NavigationBar(
        containerColor = NumoColors.surface,
        tonalElevation = 0.dp
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { onNavegar(item.ruta) },
                icon = {
                    Icon(
                        imageVector = item.icono,
                        contentDescription = item.etiqueta
                    )
                },
                label = {
                    Text(
                        text = item.etiqueta,
                        fontFamily = Jost,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NumoColors.verde,
                    selectedTextColor = NumoColors.verde,
                    indicatorColor = NumoColors.surface,
                    unselectedIconColor = NumoColors.textoTerciario,
                    unselectedTextColor = NumoColors.textoTerciario
                )
            )
        }
    }
}