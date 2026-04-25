package com.alexiaherrador.numo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Verde,
    onPrimary = CremaSuave,
    primaryContainer = VerdeMedio,
    onPrimaryContainer = CremaSuave,
    secondary = Ambar,
    onSecondary = TextoPrimario,
    error = Rojo,
    onError = CremaSuave,
    background = Crema,
    onBackground = TextoPrimario,
    surface = CremaSuave,
    onSurface = TextoPrimario,
    surfaceVariant = CremaOscura,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdePastel,
    onPrimary = VerdeOscuro,
    primaryContainer = Verde,
    onPrimaryContainer = CremaSuave,
    secondary = Ambar,
    onSecondary = TextoPrimario,
    error = Rojo,
    onError = CremaSuave,
    background = FondoOscuro,
    onBackground = Crema,
    surface = SuperficieOscura,
    onSurface = Crema,
    surfaceVariant = Color(0xFF2A3A2D),
    onSurfaceVariant = TextoTerciario,
    outline = Color(0xFF3A4A3D),
)

object NumoColors {
    var isDark = false
    var moneda = "€"

    val background @Composable get() = if (isDark) FondoOscuro else Crema
    val surface @Composable get() = if (isDark) SuperficieOscura else CremaSuave
    val surface2 @Composable get() = if (isDark) Color(0xFF2A3A2D) else CremaOscura
    val textoPrimario @Composable get() = if (isDark) Crema else TextoPrimario
    val textoSecundario @Composable get() = if (isDark) Color(0xFFB0A090) else TextoSecundario
    val textoTerciario @Composable get() = if (isDark) Color(0xFF7A6F64) else TextoTerciario
    val borde @Composable get() = if (isDark) Color(0xFF3A4A3D) else Borde
    val verde @Composable get() = if (isDark) VerdePastel else Verde
    val verdeOscuro @Composable get() = if (isDark) Color(0xFF0D1F14) else VerdeOscuro

    val sobreVerde @Composable get() = if (isDark) Color.Black else Color.White
    val sobreVerdeSubtitle @Composable get() = if (isDark) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f)
    val sobreVerdeBorde @Composable get() = if (isDark) Color.Black.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.4f)
    val sobreVerdeContainer @Composable get() = if (isDark) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f)
}

@Composable
fun NumoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NumoTypography,
        content = content
    )
}