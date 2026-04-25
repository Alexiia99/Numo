package com.alexiaherrador.numo.ui.screens.estadisticas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.domain.model.Categoria
import com.alexiaherrador.numo.ui.theme.*

// Colores para el gráfico de tarta
val coloresGrafico = listOf(
    Color(0xFF2D5A3D),
    Color(0xFFE8A838),
    Color(0xFFC94F3A),
    Color(0xFF6B9BD2),
    Color(0xFFA8A8D8),
    Color(0xFF7BC67E),
    Color(0xFFFFB347),
    Color(0xFFB39DDB)
)

@Composable
fun EstadisticasScreen(
    viewModel: EstadisticasViewModel,
    onNavigate: (String) -> Unit
) {
    val gastosPorCategoria by viewModel.gastosPorCategoria.collectAsState()
    val evolucionMensual by viewModel.evolucionMensual.collectAsState()
    val ahorroAcumulado by viewModel.ahorroAcumulado.collectAsState()

    val totalGastado = gastosPorCategoria.values.sum()

    Scaffold(containerColor = NumoColors.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Cabecera
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NumoColors.verde)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Estadísticas",
                        fontFamily = PlayfairDisplay,
                        fontSize = 24.sp,
                        color = NumoColors.sobreVerde
                    )
                }
                HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
            }

            // Tarjeta ahorro
            if (ahorroAcumulado > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        colors = CardDefaults.cardColors(containerColor= NumoColors.verdeOscuro),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "🌱 Ahorro acumulado",
                                fontFamily = Jost,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp,
                                color = Ambar
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${String.format("%.2f", ahorroAcumulado)} ${NumoColors.moneda}",
                                fontFamily = PlayfairDisplay,
                                fontSize = 32.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Gráfico tarta
            if (gastosPorCategoria.isNotEmpty() && totalGastado > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(NumoColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Gastos por categoría",
                                fontFamily = PlayfairDisplay,
                                fontSize = 16.sp,
                                color = NumoColors.textoPrimario,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Gráfico
                                GraficoTarta(
                                    datos = gastosPorCategoria,
                                    total = totalGastado,
                                    modifier = Modifier.size(120.dp)
                                )
                                // Leyenda
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    gastosPorCategoria.entries
                                        .sortedByDescending { it.value }
                                        .forEachIndexed { index, (categoria, gasto) ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            coloresGrafico[index % coloresGrafico.size],
                                                            RoundedCornerShape(50)
                                                        )
                                                )
                                                Text(
                                                    categoria.nombreMostrar,
                                                    fontFamily = Jost,
                                                    fontSize = 10.sp,
                                                    color = NumoColors.textoSecundario,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(
                                                    "${((gasto / totalGastado) * 100).toInt()}%",
                                                    fontFamily = Jost,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 10.sp,
                                                    color = NumoColors.textoPrimario
                                                )
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }

            // Evolución mensual
            if (evolucionMensual.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(NumoColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Evolución mensual",
                                fontFamily = PlayfairDisplay,
                                fontSize = 16.sp,
                                color = NumoColors.textoPrimario,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            GraficoBarras(
                                datos = evolucionMensual,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    }
                }
            }

            // Comparativa mes anterior
            if (evolucionMensual.size >= 2) {
                item {
                    val mesActual = evolucionMensual.last()
                    val mesAnterior = evolucionMensual[evolucionMensual.size - 2]
                    val diferencia = mesActual.second - mesAnterior.second
                    val porcentaje = if (mesAnterior.second > 0)
                        ((diferencia / mesAnterior.second) * 100).toInt() else 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(NumoColors.surface),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(mesAnterior.first, fontFamily = Jost, fontSize = 10.sp, color = NumoColors.textoTerciario)
                                Text(
                                    "${String.format("%.0f", mesAnterior.second)} ${NumoColors.moneda}",
                                    fontFamily = PlayfairDisplay,
                                    fontSize = 20.sp,
                                    color = NumoColors.textoPrimario
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (diferencia <= 0) Color(0xFFE8F5EE) else Color(0xFFFDECEA),
                                            RoundedCornerShape(20.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${if (diferencia <= 0) "▼" else "▲"} ${Math.abs(porcentaje)}%",
                                        fontFamily = Jost,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = if (diferencia <= 0) Verde else Rojo
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(mesActual.first, fontFamily = Jost, fontSize = 10.sp, color = NumoColors.textoTerciario)
                                Text(
                                    "${String.format("%.0f", mesActual.second)} ${NumoColors.moneda}",
                                    fontFamily = PlayfairDisplay,
                                    fontSize = 20.sp,
                                    color = NumoColors.textoPrimario
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GraficoTarta(
    datos: Map<Categoria, Double>,
    total: Double,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        var anguloInicio = -90f
        datos.entries
            .sortedByDescending { it.value }
            .forEachIndexed { index, (_, valor) ->
                val angulo = ((valor / total) * 360f).toFloat()
                drawArc(
                    color = coloresGrafico[index % coloresGrafico.size],
                    startAngle = anguloInicio,
                    sweepAngle = angulo,
                    useCenter = true,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                    size = Size(size.width * 0.8f, size.height * 0.8f)
                )
                anguloInicio += angulo
            }
    }
}

@Composable
private fun GraficoBarras(
    datos: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    val maximo = datos.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0

    Column(modifier = modifier) {
        // Barras solas arriba
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            datos.forEach { (etiqueta, valor) ->
                val esUltimo = etiqueta == datos.last().first
                val alturaFraccion = (valor / maximo).toFloat().coerceAtLeast(0.05f)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(alturaFraccion)
                        .background(
                            if (esUltimo) Verde else CremaOscura,
                            RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Etiquetas abajo separadas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            datos.forEach { (etiqueta, _) ->
                val esUltimo = etiqueta == datos.last().first
                Text(
                    text = etiqueta,
                    modifier = Modifier.weight(1f),
                    fontFamily = Jost,
                    fontSize = 8.sp,
                    color = if (esUltimo) Verde else TextoTerciario,
                    fontWeight = if (esUltimo) FontWeight.Bold else FontWeight.Normal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}