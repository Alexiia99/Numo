package com.alexiaherrador.numo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.R

val Jost = FontFamily(
    Font(R.font.jost_regular, FontWeight.Normal),
    Font(R.font.jost_medium, FontWeight.Medium),
    Font(R.font.jost_semibold, FontWeight.SemiBold),
)

val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display_regular, FontWeight.Normal),
    Font(R.font.playfair_display_semibold, FontWeight.SemiBold),
)

val NumoTypography = Typography(
    // Títulos grandes e importes — Playfair Display
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = (-1.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        letterSpacing = (-1).sp
    ),
    displaySmall = TextStyle(
        fontFamily = PlayfairDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
    ),

    // Cabeceras — Jost
    headlineLarge = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
    ),

    // Cuerpo
    bodyLarge = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
    ),

    // Labels
    labelLarge = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Jost,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 0.5.sp
    )
)