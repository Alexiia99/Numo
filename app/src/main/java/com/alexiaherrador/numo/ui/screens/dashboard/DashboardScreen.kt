package com.alexiaherrador.numo.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alexiaherrador.numo.ui.components.TarjetaGasto
import com.alexiaherrador.numo.ui.navigation.Rutas
import com.alexiaherrador.numo.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigate: (String) -> Unit
) {
    val nombreUsuario by viewModel.nombreUsuario.collectAsState()
    val avatarUsuario by viewModel.avatarUsuario.collectAsState()
    val fotoUri by viewModel.fotoUri.collectAsState()
    val gastosMes by viewModel.gastosMes.collectAsState()
    val totalGastado by viewModel.totalGastado.collectAsState()
    val ahorroAcumulado by viewModel.ahorroAcumulado.collectAsState()
    val alertas by viewModel.alertas.collectAsState()
    val saldoRestante by viewModel.saldoRestante.collectAsState()


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(Rutas.AnadirGasto.ruta) },
                containerColor = NumoColors.verde,
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Text("+", fontSize = 28.sp, lineHeight = 28.sp)
            }
        },
        containerColor = NumoColors.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { CabeceraUsuario(totalGastado, nombreUsuario, avatarUsuario, fotoUri, saldoRestante) }

            if (ahorroAcumulado > 0) {
                item { TarjetaAhorro(ahorroAcumulado) }
            }

            if (alertas.isNotEmpty()) {
                items(alertas) { alerta ->
                    BannerAlerta(
                        mensaje = alerta,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 3.dp)
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            item {
                Text(
                    text = "Últimos movimientos",
                    fontFamily = PlayfairDisplay,
                    fontSize = 18.sp,
                    color = NumoColors.textoPrimario,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }

            if (gastosMes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aún no hay gastos este mes ",
                            fontFamily = Jost,
                            fontSize = 13.sp,
                            color = NumoColors.textoTerciario
                        )
                    }
                }
            } else {
                items(gastosMes.take(5)) { gasto ->
                    TarjetaGasto(
                        gasto = gasto,
                        onEliminar = { viewModel.eliminarGasto(it) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 18.dp),
                        color = NumoColors.borde,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun CabeceraUsuario(
    totalGastado: Double,
    nombre: String,
    avatar: String,
    fotoUri: String?,
    saldoRestante: Double?

) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NumoColors.verde)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (fotoUri != null) {
                    AsyncImage(
                        model = fotoUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NumoColors.sobreVerdeContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatar, fontSize = 22.sp)
                    }
                }

                Column {
                    Text(
                        text = "Buenas",
                        fontFamily = Jost,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = NumoColors.sobreVerdeSubtitle
                    )
                    Text(
                        text = nombre,
                        fontFamily = Jost,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = NumoColors.sobreVerde
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Gastado este mes",
                        fontFamily = Jost,
                        fontSize = 14.sp,
                        color = NumoColors.sobreVerdeSubtitle
                    )
                    Text(
                        text = "${String.format("%.2f", totalGastado)} ${NumoColors.moneda}",
                        fontFamily = PlayfairDisplay,
                        fontSize = 42.sp,
                        color = NumoColors.sobreVerde
                    )
                }

                if (saldoRestante != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Restante",
                            fontFamily = Jost,
                            fontSize = 11.sp,
                            color = NumoColors.sobreVerdeSubtitle
                        )
                        Text(
                            text = "${String.format("%.2f", saldoRestante)} ${NumoColors.moneda}",
                            fontFamily = PlayfairDisplay,
                            fontSize = 22.sp,
                            color = if (saldoRestante >= 0) NumoColors.sobreVerde else Ambar,
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun TarjetaAhorro(ahorro: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        colors = CardDefaults.cardColors(containerColor = NumoColors.verdeOscuro),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ahorro acumulado",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Ambar,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${String.format("%.2f", ahorro)} ${NumoColors.moneda}",
                fontFamily = PlayfairDisplay,
                fontSize = 34.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Desde que usas Numo",
                fontFamily = Jost,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.45f)
            )
        }
    }
}

@Composable
private fun BannerAlerta(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("⚠️", fontSize = 14.sp)
        Text(
            text = mensaje,
            fontFamily = Jost,
            fontSize = 11.sp,
            color = Color(0xFFB8680A)
        )
    }
}