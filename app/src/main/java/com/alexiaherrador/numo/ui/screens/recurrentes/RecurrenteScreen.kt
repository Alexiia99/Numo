package com.alexiaherrador.numo.ui.screens.recurrentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.domain.model.Categoria
import com.alexiaherrador.numo.domain.model.GastoRecurrente
import com.alexiaherrador.numo.domain.model.Periodicidad
import com.alexiaherrador.numo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RecurrentesScreen(
    viewModel: RecurrentesViewModel,
    onNavigate: (String) -> Unit
) {
    val recurrentes by viewModel.recurrentes.collectAsState()
    val pendientes by viewModel.pendientes.collectAsState()
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var gastoAEliminar by remember { mutableStateOf<GastoRecurrente?>(null) }

    val totalMensual = recurrentes.sumOf { gasto ->
        when (gasto.periodicidad) {
            Periodicidad.MENSUAL -> gasto.cantidad
            Periodicidad.TRIMESTRAL -> gasto.cantidad / 3
            Periodicidad.ANUAL -> gasto.cantidad / 12
        }
    }

    Scaffold(
        containerColor = NumoColors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogoNuevo = true },
                containerColor = NumoColors.verde,
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Añadir recurrente")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NumoColors.verde)
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Recurrentes",
                        fontFamily = PlayfairDisplay,
                        fontSize = 24.sp,
                        color = NumoColors.sobreVerde
                    )
                    Text(
                        text = "Gastos que se repiten automáticamente",
                        fontFamily = Jost,
                        fontSize = 11.sp,
                        color = NumoColors.sobreVerdeSubtitle
                    )
                }
                HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TarjetaResumen(
                        label = "Al mes",
                        valor = "${String.format("%.2f", totalMensual)} ${NumoColors.moneda}",
                        colorValor = NumoColors.verde,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        label = "Pendientes",
                        valor = "${pendientes.size}",
                        colorValor = if (pendientes.isNotEmpty()) Ambar else NumoColors.textoPrimario,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        label = "Activos",
                        valor = "${recurrentes.size}",
                        colorValor = NumoColors.textoPrimario,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (pendientes.isNotEmpty()) {
                item {
                    SeccionLabel(
                        texto = "⏳ Pendientes de confirmar",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                items(pendientes) { gasto ->
                    TarjetaPendiente(
                        gasto = gasto,
                        onConfirmar = { viewModel.confirmarRecurrente(gasto) },
                        onIgnorar = { viewModel.ignorarRecurrente(gasto) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            item {
                SeccionLabel(
                    texto = "📋 Todos los recurrentes",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (recurrentes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay gastos recurrentes\nPulsa + para añadir uno 🌱",
                            fontFamily = Jost,
                            fontSize = 13.sp,
                            color = NumoColors.textoTerciario,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(recurrentes) { gasto ->
                    TarjetaRecurrente(
                        gasto = gasto,
                        onEliminar = { gastoAEliminar = gasto },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (mostrarDialogoNuevo) {
        DialogoNuevoRecurrente(
            onConfirmar = { gasto ->
                viewModel.guardarRecurrente(gasto)
                mostrarDialogoNuevo = false
            },
            onDismiss = { mostrarDialogoNuevo = false }
        )
    }

    gastoAEliminar?.let { gasto ->
        AlertDialog(
            onDismissRequest = { gastoAEliminar = null },
            containerColor = NumoColors.surface,
            title = {
                Text(
                    "¿Eliminar recurrente?",
                    fontFamily = PlayfairDisplay,
                    fontSize = 20.sp,
                    color = NumoColors.textoPrimario
                )
            },
            text = {
                Text(
                    "Se eliminará \"${gasto.nombre}\" de ${String.format("%.2f", gasto.cantidad)} ${NumoColors.moneda} y dejará de recordártelo cada ${gasto.periodicidad.label.lowercase()}. Esta acción no se puede deshacer.",
                    fontFamily = Jost,
                    fontSize = 13.sp,
                    color = NumoColors.textoSecundario
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarRecurrente(gasto)
                    gastoAEliminar = null
                }) {
                    Text(
                        "Eliminar",
                        fontFamily = Jost,
                        fontWeight = FontWeight.SemiBold,
                        color = Rojo
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { gastoAEliminar = null }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }
}

@Composable
private fun TarjetaResumen(
    label: String,
    valor: String,
    colorValor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(NumoColors.surface)
            .border(1.dp, NumoColors.borde, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontFamily = Jost, fontSize = 8.sp, color = NumoColors.textoTerciario, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = valor, fontFamily = PlayfairDisplay, fontSize = 15.sp, color = colorValor)
    }
}

@Composable
private fun TarjetaPendiente(
    gasto: GastoRecurrente,
    onConfirmar: () -> Unit,
    onIgnorar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diasRestantes = ((gasto.proximoPago - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)).toInt()
    val textoVence = when {
        diasRestantes <= 0 -> "⚠️ Vence hoy"
        diasRestantes == 1 -> "⚠️ Vence mañana"
        else -> "⚠️ Vence en $diasRestantes días"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF8E7))
            .border(1.5.dp, Ambar, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = textoVence,
            fontFamily = Jost,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            color = Color(0xFFB8680A),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFE9B0)),
                contentAlignment = Alignment.Center
            ) {
                Text(gasto.categoria.emoji, fontSize = 18.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = gasto.nombre, fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = NumoColors.textoPrimario)
                Text(text = "${gasto.periodicidad.label} · ${gasto.categoria.nombreMostrar}", fontFamily = Jost, fontSize = 10.sp, color = NumoColors.textoTerciario)
            }
            Text(
                text = "-${String.format("%.2f", gasto.cantidad)} ${NumoColors.moneda}",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Rojo
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onConfirmar,
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NumoColors.verde)
            ) {
                Text("✓ Confirmar", fontFamily = Jost, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            OutlinedButton(
                onClick = onIgnorar,
                modifier = Modifier.weight(1f).height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NumoColors.textoSecundario)
            ) {
                Text("Ignorar", fontFamily = Jost, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TarjetaRecurrente(
    gasto: GastoRecurrente,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("d MMM", Locale("es"))
    val proximoPagoStr = formatter.format(Date(gasto.proximoPago))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NumoColors.surface)
            .border(1.dp, NumoColors.borde, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NumoColors.surface2),
            contentAlignment = Alignment.Center
        ) {
            Text(gasto.categoria.emoji, fontSize = 18.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = gasto.nombre, fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = NumoColors.textoPrimario)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(NumoColors.surface2)
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(text = gasto.periodicidad.label, fontFamily = Jost, fontSize = 8.sp, color = NumoColors.textoSecundario)
                }
                Text(text = "· ${gasto.categoria.nombreMostrar}", fontFamily = Jost, fontSize = 9.sp, color = NumoColors.textoTerciario)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "-${String.format("%.2f", gasto.cantidad)} ${NumoColors.moneda}",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Rojo
            )
            Text(text = "Próximo: $proximoPagoStr", fontFamily = Jost, fontSize = 9.sp, color = NumoColors.textoTerciario)
        }
        IconButton(
            onClick = onEliminar,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Rounded.Delete, contentDescription = "Eliminar", tint = NumoColors.textoTerciario, modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun SeccionLabel(texto: String, modifier: Modifier = Modifier) {
    Text(text = texto, fontFamily = Jost, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, color = NumoColors.textoTerciario, modifier = modifier)
}

@Composable
private fun DialogoNuevoRecurrente(
    onConfirmar: (GastoRecurrente) -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf(Categoria.OTROS) }
    var periodicidadSeleccionada by remember { mutableStateOf(Periodicidad.MENSUAL) }
    var diasAviso by remember { mutableStateOf(1) }
    var diaDelMes by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    // ← Fix: campo como String para poder borrar y reescribir libremente
    var diaDelMesInput by remember { mutableStateOf(diaDelMes.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NumoColors.surface,
        title = {
            Text("Nuevo recurrente", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Text("Nombre", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = { Text("Ej: Netflix", fontFamily = Jost, color = NumoColors.textoTerciario) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NumoColors.verde, unfocusedBorderColor = NumoColors.borde,
                        cursorColor = NumoColors.verde, focusedTextColor = NumoColors.textoPrimario, unfocusedTextColor = NumoColors.textoPrimario
                    ),
                    textStyle = TextStyle(fontFamily = Jost),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Cantidad", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    placeholder = { Text("0,00 ${NumoColors.moneda}", fontFamily = Jost, color = NumoColors.textoTerciario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NumoColors.verde, unfocusedBorderColor = NumoColors.borde,
                        cursorColor = NumoColors.verde, focusedTextColor = NumoColors.textoPrimario, unfocusedTextColor = NumoColors.textoPrimario
                    ),
                    textStyle = TextStyle(fontFamily = Jost),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Categoría", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Categoria.values().take(4).forEach { cat ->
                        val sel = cat == categoriaSeleccionada
                        Column(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .background(if (sel) NumoColors.verde.copy(alpha = 0.12f) else NumoColors.surface2)
                                .border(if (sel) 1.5.dp else 1.dp, if (sel) NumoColors.verde else NumoColors.borde, RoundedCornerShape(8.dp))
                                .clickable { categoriaSeleccionada = cat }.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(cat.emoji, fontSize = 16.sp)
                            Text(cat.nombreMostrar, fontFamily = Jost, fontSize = 7.sp, color = if (sel) NumoColors.verde else NumoColors.textoTerciario, textAlign = TextAlign.Center, maxLines = 1)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Categoria.values().drop(4).forEach { cat ->
                        val sel = cat == categoriaSeleccionada
                        Column(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .background(if (sel) NumoColors.verde.copy(alpha = 0.12f) else NumoColors.surface2)
                                .border(if (sel) 1.5.dp else 1.dp, if (sel) NumoColors.verde else NumoColors.borde, RoundedCornerShape(8.dp))
                                .clickable { categoriaSeleccionada = cat }.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(cat.emoji, fontSize = 16.sp)
                            Text(cat.nombreMostrar, fontFamily = Jost, fontSize = 7.sp, color = if (sel) NumoColors.verde else NumoColors.textoTerciario, textAlign = TextAlign.Center, maxLines = 1)
                        }
                    }
                }

                Text("Periodicidad", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Periodicidad.values().forEach { periodo ->
                        val sel = periodo == periodicidadSeleccionada
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .background(if (sel) NumoColors.verde.copy(alpha = 0.12f) else NumoColors.surface2)
                                .border(if (sel) 1.5.dp else 1.dp, if (sel) NumoColors.verde else NumoColors.borde, RoundedCornerShape(8.dp))
                                .clickable { periodicidadSeleccionada = periodo }.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(periodo.label, fontFamily = Jost, fontSize = 10.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) NumoColors.verde else NumoColors.textoSecundario)
                        }
                    }
                }

                Text("Día de cobro", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                OutlinedTextField(
                    value = diaDelMesInput,
                    onValueChange = { input ->
                        // Permite campo vacío o números de hasta 2 dígitos
                        if (input.length <= 2 && (input.isEmpty() || input.toIntOrNull() != null)) {
                            diaDelMesInput = input
                            input.toIntOrNull()?.coerceIn(1, 31)?.let { d -> diaDelMes = d }
                        }
                    },
                    placeholder = { Text("1-31", fontFamily = Jost, color = NumoColors.textoTerciario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NumoColors.verde, unfocusedBorderColor = NumoColors.borde,
                        cursorColor = NumoColors.verde, focusedTextColor = NumoColors.textoPrimario, unfocusedTextColor = NumoColors.textoPrimario
                    ),
                    textStyle = TextStyle(fontFamily = Jost),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Avisarme", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1 to "1 día antes", 3 to "3 días antes", 0 to "El mismo día").forEach { (dias, label) ->
                        val sel = diasAviso == dias
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .background(if (sel) NumoColors.verde.copy(alpha = 0.12f) else NumoColors.surface2)
                                .border(if (sel) 1.5.dp else 1.dp, if (sel) NumoColors.verde else NumoColors.borde, RoundedCornerShape(8.dp))
                                .clickable { diasAviso = dias }.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, fontFamily = Jost, fontSize = 9.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal, color = if (sel) NumoColors.verde else NumoColors.textoSecundario, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val cantidadDouble = cantidad.toDoubleOrNull() ?: return@TextButton
                    if (nombre.isBlank()) return@TextButton
                    onConfirmar(
                        GastoRecurrente(
                            nombre = nombre.trim(),
                            cantidad = cantidadDouble,
                            categoria = categoriaSeleccionada,
                            periodicidad = periodicidadSeleccionada,
                            proximoPago = calcularPrimerPago(periodicidadSeleccionada, diaDelMes),
                            diasAviso = diasAviso
                        )
                    )
                },
                enabled = nombre.isNotBlank() && cantidad.isNotBlank()
            ) {
                Text("Guardar", fontFamily = Jost, fontWeight = FontWeight.SemiBold, color = NumoColors.verde)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
            }
        }
    )
}

private fun calcularPrimerPago(periodicidad: Periodicidad, diaDelMes: Int): Long {
    val cal = Calendar.getInstance().apply {
        when (periodicidad) {
            Periodicidad.MENSUAL -> add(Calendar.MONTH, 1)
            Periodicidad.TRIMESTRAL -> add(Calendar.MONTH, 3)
            Periodicidad.ANUAL -> add(Calendar.YEAR, 1)
        }
        set(Calendar.DAY_OF_MONTH, diaDelMes.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)))
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}