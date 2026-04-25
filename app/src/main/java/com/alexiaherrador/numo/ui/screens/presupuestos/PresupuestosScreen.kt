package com.alexiaherrador.numo.ui.screens.presupuestos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.domain.model.Categoria
import com.alexiaherrador.numo.ui.components.BarraPresupuesto
import com.alexiaherrador.numo.ui.theme.*
import java.util.Calendar

@Composable
fun PresupuestosScreen(
    viewModel: PresupuestosViewModel,
    onNavigate: (String) -> Unit
) {
    val presupuestos by viewModel.presupuestos.collectAsState()
    val gastosPorCategoria by viewModel.gastosPorCategoria.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var categoriaDialogo by remember { mutableStateOf<Categoria?>(null) }

    val mesActual = Calendar.getInstance().get(Calendar.MONTH) + 1
    val anioActual = Calendar.getInstance().get(Calendar.YEAR)

    Scaffold(
        containerColor = NumoColors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor= NumoColors.verde,
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Añadir tope")
            }
        }
    ) { padding ->
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
                        text = "Mis topes",
                        fontFamily = PlayfairDisplay,
                        fontSize = 24.sp,
                        color = NumoColors.sobreVerde
                    )
                    Text(
                        text = "Gestiona tus límites mensuales",
                        fontFamily = Jost,
                        fontSize = 14.sp,
                        color = NumoColors.sobreVerdeSubtitle
                    )
                }
                HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
            }

            // Lista de presupuestos
            if (presupuestos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay topes configurados\nPulsa + para añadir uno ",
                            fontFamily = Jost,
                            fontSize = 13.sp,
                            color = NumoColors.textoTerciario,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(presupuestos) { presupuesto ->
                    val gastado = gastosPorCategoria[presupuesto.categoria] ?: 0.0
                    val categoria = Categoria.valueOf(presupuesto.categoria)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(NumoColors.surface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            BarraPresupuesto(
                                nombreCategoria = categoria.nombreMostrar,
                                emoji = categoria.emoji,
                                gastado = gastado,
                                tope = presupuesto.tope
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        categoriaDialogo = categoria
                                        mostrarDialogo = true
                                    }
                                ) {
                                    Text(
                                        "Editar tope",
                                        fontFamily = Jost,
                                        fontSize = 12.sp,
                                        color= NumoColors.verde
                                    )
                                }
                                TextButton(
                                    onClick = { viewModel.eliminarPresupuesto(presupuesto) }
                                ) {
                                    Text(
                                        "Eliminar",
                                        fontFamily = Jost,
                                        fontSize = 12.sp,
                                        color = Rojo
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo añadir/editar tope
    if (mostrarDialogo) {
        DialogoTope(
            categoriaInicial = categoriaDialogo,
            onConfirmar = { categoria, tope ->
                viewModel.guardarPresupuesto(categoria.name, tope)
                mostrarDialogo = false
                categoriaDialogo = null
            },
            onDismiss = {
                mostrarDialogo = false
                categoriaDialogo = null
            }
        )
    }
}

@Composable
private fun DialogoTope(
    categoriaInicial: Categoria?,
    onConfirmar: (Categoria, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf(categoriaInicial ?: Categoria.OTROS) }
    var topeInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NumoColors.surface,
        title = {
            Text(
                "Nuevo tope",
                fontFamily = PlayfairDisplay,
                fontSize = 20.sp,
                color = NumoColors.textoPrimario
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Selector categoría
                Text("Categoría", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                ExposedDropdownMenuCategorias(
                    seleccionada = categoriaSeleccionada,
                    onSeleccion = { categoriaSeleccionada = it }
                )
                // Input tope
                Text("Tope mensual (${NumoColors.moneda})", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                OutlinedTextField(
                    value = topeInput,
                    onValueChange = { topeInput = it },
                    placeholder = { Text("0,00 ${NumoColors.moneda}", fontFamily = Jost, color = NumoColors.textoTerciario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor= NumoColors.verde,
                        unfocusedBorderColor = NumoColors.borde,
                        cursorColor= NumoColors.verde
                    ),
                    textStyle = TextStyle(fontFamily = Jost),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val tope = topeInput.toDoubleOrNull() ?: return@TextButton
                    onConfirmar(categoriaSeleccionada, tope)
                },
                enabled = topeInput.isNotBlank()
            ) {
                Text("Guardar", fontFamily = Jost, fontWeight = FontWeight.SemiBold, color= NumoColors.verde)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuCategorias(
    seleccionada: Categoria,
    onSeleccion: (Categoria) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }
    ) {
        OutlinedTextField(
            value = "${seleccionada.emoji} ${seleccionada.nombreMostrar}",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor= NumoColors.verde,
                unfocusedBorderColor = NumoColors.borde
            ),
            textStyle = TextStyle(fontFamily = Jost, fontSize = 13.sp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(NumoColors.surface)
        ) {
            Categoria.values().forEach { categoria ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "${categoria.emoji} ${categoria.nombreMostrar}",
                            fontFamily = Jost,
                            fontSize = 13.sp
                        )
                    },
                    onClick = {
                        onSeleccion(categoria)
                        expandido = false
                    }
                )
            }
        }
    }
}