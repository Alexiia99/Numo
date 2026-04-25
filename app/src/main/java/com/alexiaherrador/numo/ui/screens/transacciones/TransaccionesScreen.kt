package com.alexiaherrador.numo.ui.screens.transacciones

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.domain.model.Categoria
import com.alexiaherrador.numo.ui.components.TarjetaGasto
import com.alexiaherrador.numo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransaccionesScreen(
    viewModel: TransaccionesViewModel,
    onNavigate: (String) -> Unit
) {
    val gastosFiltrados by viewModel.gastosFiltrados.collectAsState()
    val filtroCategoria by viewModel.filtroCategoria.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()

    val gastosAgrupados = remember(gastosFiltrados) {
        gastosFiltrados.groupBy { gasto ->
            SimpleDateFormat("dd MMM yyyy", Locale("es")).format(Date(gasto.fecha))
        }
    }

    Scaffold(containerColor = NumoColors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Cabecera
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NumoColors.verde)
                    .padding(18.dp),
            ) {
                Text(
                    text = "Mis gastos",
                    fontFamily = PlayfairDisplay,
                    fontSize = 24.sp,
                    color = NumoColors.sobreVerde,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Buscador
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { viewModel.onBusquedaCambiada(it) },
                    placeholder = {
                        Text("Buscar transacción...", fontFamily = Jost, fontSize = 15.sp, color = NumoColors.sobreVerdeSubtitle)
                    },
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null, tint = NumoColors.sobreVerdeSubtitle)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NumoColors.sobreVerde,
                        unfocusedBorderColor = NumoColors.sobreVerdeBorde,
                        cursorColor = NumoColors.sobreVerde,
                        focusedTextColor = NumoColors.sobreVerde,
                        unfocusedTextColor = NumoColors.sobreVerde,
                        focusedContainerColor = NumoColors.sobreVerdeContainer,
                        unfocusedContainerColor = NumoColors.sobreVerdeContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                // Chips de categoría
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = filtroCategoria == null,
                        onClick = { viewModel.onFiltroCategoria(null) },
                        label = { Text("Todos", fontFamily = Jost, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = NumoColors.sobreVerdeContainer,
                            labelColor = NumoColors.sobreVerde,
                            selectedContainerColor = NumoColors.sobreVerde,
                            selectedLabelColor = NumoColors.verde
                        )
                    )
                    Categoria.values().forEach { categoria ->
                        FilterChip(
                            selected = filtroCategoria == categoria.name,
                            onClick = { viewModel.onFiltroCategoria(categoria.name) },
                            label = {
                                Text(
                                    "${categoria.emoji} ${categoria.nombreMostrar}",
                                    fontFamily = Jost,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = NumoColors.sobreVerdeContainer,
                                labelColor = NumoColors.sobreVerde,
                                selectedContainerColor = NumoColors.sobreVerde,
                                selectedLabelColor = NumoColors.verde
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)

            if (gastosAgrupados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "No hay gastos",
                        fontFamily = Jost,
                        fontSize = 14.sp,
                        color = NumoColors.textoTerciario
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    gastosAgrupados.forEach { (fecha, gastos) ->
                        item {
                            Text(
                                text = fecha,
                                fontFamily = Jost,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = NumoColors.textoTerciario,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(
                                    horizontal = 18.dp,
                                    vertical = 8.dp
                                )
                            )
                        }
                        items(gastos) { gasto ->
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
    }
}