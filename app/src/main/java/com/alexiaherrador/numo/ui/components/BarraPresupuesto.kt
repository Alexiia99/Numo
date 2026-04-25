package com.alexiaherrador.numo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.ui.theme.Ambar
import com.alexiaherrador.numo.ui.theme.CremaOscura
import com.alexiaherrador.numo.ui.theme.Jost
import com.alexiaherrador.numo.ui.theme.NumoColors
import com.alexiaherrador.numo.ui.theme.Rojo
import com.alexiaherrador.numo.ui.theme.TextoPrimario
import com.alexiaherrador.numo.ui.theme.TextoTerciario
import com.alexiaherrador.numo.ui.theme.Verde

@Composable
fun BarraPresupuesto(
    nombreCategoria: String,
    emoji: String,
    gastado: Double,
    tope: Double,
    modifier: Modifier = Modifier
) {
    val porcentaje = if (tope > 0) (gastado / tope).coerceIn(0.0, 1.0).toFloat() else 0f
    val colorBarra = when {
        porcentaje >= 1f -> Rojo
        porcentaje >= 0.8f -> Ambar
        else -> Verde
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$emoji $nombreCategoria",
                fontFamily = Jost,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = NumoColors.textoPrimario
            )
            Text(
                text = "${String.format("%.0f", gastado)} € / ${String.format("%.0f", tope)} ${NumoColors.moneda}",
                fontFamily = Jost,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = NumoColors.textoTerciario
            )
        }

        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CremaOscura)
        ) {
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(porcentaje)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(colorBarra)
            )
        }
    }
}