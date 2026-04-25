package com.alexiaherrador.numo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.domain.model.Gasto
import com.alexiaherrador.numo.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaGasto(
    gasto: Gasto,
    onEliminar: (Gasto) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val esRecurrente = gasto.notaOpcional?.startsWith("Recurrente") == true

    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                mostrarDialogo = true
            }
            false
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (swipeState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    Color(0xFFC94F3A) else Color.Transparent,
                label = "swipe_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(NumoColors.background)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono categoría
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NumoColors.surface2),
                contentAlignment = Alignment.Center
            ) {
                Text(text = gasto.categoria.emoji, fontSize = 18.sp)
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gasto.descripcion,
                    fontFamily = Jost,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = NumoColors.textoPrimario
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = gasto.categoria.nombreMostrar,
                        fontFamily = Jost,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        color = NumoColors.textoTerciario
                    )
                    // Badge recurrente
                    if (esRecurrente) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(NumoColors.verde.copy(alpha = 0.12f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "↺ recurrente",
                                fontFamily = Jost,
                                fontSize = 8.sp,
                                color = NumoColors.verde
                            )
                        }
                    }
                }
            }

            // Cantidad y fecha
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-${String.format("%.2f", gasto.cantidad)} ${NumoColors.moneda}",
                    fontFamily = Jost,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Rojo
                )
                Text(
                    text = SimpleDateFormat("dd MMM", Locale("es")).format(Date(gasto.fecha)),
                    fontFamily = Jost,
                    fontWeight = FontWeight.Normal,
                    fontSize = 9.sp,
                    color = NumoColors.textoTerciario
                )
            }

            // Botón papelera
            IconButton(
                onClick = { mostrarDialogo = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Eliminar",
                    tint = NumoColors.textoTerciario,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    // Diálogo confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            containerColor = NumoColors.surface,
            title = {
                Text(
                    text = if (esRecurrente) "⚠️ Gasto recurrente" else "¿Eliminar gasto?",
                    fontFamily = PlayfairDisplay,
                    fontSize = 20.sp,
                    color = NumoColors.textoPrimario
                )
            },
            text = {
                Text(
                    text = if (esRecurrente)
                        "\"${gasto.descripcion}\" es un gasto recurrente. Si lo eliminas solo borrarás este registro, pero el recurrente seguirá activo y volverá a aparecer el próximo mes.\n\n¿Quieres eliminarlo igualmente?"
                    else
                        "Se eliminará \"${gasto.descripcion}\" de ${String.format("%.2f", gasto.cantidad)} ${NumoColors.moneda}. Esta acción no se puede deshacer.",
                    fontFamily = Jost,
                    fontSize = 13.sp,
                    color = NumoColors.textoSecundario
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onEliminar(gasto)
                    mostrarDialogo = false
                }) {
                    Text(
                        text = if (esRecurrente) "Eliminar solo este" else "Eliminar",
                        fontFamily = Jost,
                        fontWeight = FontWeight.SemiBold,
                        color = Rojo
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }
}