package com.alexiaherrador.numo.ui.screens.anadir

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.domain.model.Categoria
import com.alexiaherrador.numo.ui.theme.*
import com.alexiaherrador.numo.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun AnadirGastoScreen(
    viewModel: AnadirGastoViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val cantidad by viewModel.cantidad.collectAsState()
    val descripcion by viewModel.descripcion.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val guardadoExitoso by viewModel.guardadoExitoso.collectAsState()
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    LaunchedEffect(guardadoExitoso) {
        if (guardadoExitoso) {
            soundManager.reproducirCajaRegistradora()
            delay(600)
            onGuardado()
        }
    }
    DisposableEffect(Unit) {
        onDispose { soundManager.liberar() }
    }

    Column(modifier = Modifier.fillMaxSize().background(NumoColors.background)) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NumoColors.verde)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onVolver() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Volver",
                    tint = NumoColors.sobreVerde
                )
            }
            Text(
                text = "Nuevo gasto",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = NumoColors.sobreVerde
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Verde)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nuevo gasto",
                    fontFamily = Jost,
                    fontSize = 15.sp,
                    color =  NumoColors.sobreVerdeSubtitle
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = NumoColors.moneda,
                        fontFamily = PlayfairDisplay,
                        fontSize = 40.sp,
                        color =  NumoColors.sobreVerdeSubtitle
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = cantidad,
                        onValueChange = { viewModel.onCantidadCambiada(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = TextStyle(
                            fontFamily = PlayfairDisplay,
                            fontSize = 40.sp,
                            color = NumoColors.sobreVerde,
                            textAlign = TextAlign.Center
                        ),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) {
                                if (cantidad.isEmpty()) {
                                    Text(
                                        "0,00",
                                        fontFamily = PlayfairDisplay,
                                        fontSize = 40.sp,
                                        color = NumoColors.sobreVerdeSubtitle,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }

        // Formulario
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Selector categoría
            Text(
                text = "CATEGORÍA",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = NumoColors.textoTerciario,
                letterSpacing = 0.8.sp
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(Categoria.values()) { categoria ->
                    val seleccionada = categoria == categoriaSeleccionada
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (seleccionada) Color(0xFFEEF5F0) else CremaSuave)
                            .border(
                                width = if (seleccionada) 1.5.dp else 1.dp,
                                color = if (seleccionada) Verde else Borde,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.onCategoriaSeleccionada(categoria) }
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(categoria.emoji, fontSize = 20.sp)
                        Text(
                            text = categoria.nombreMostrar,
                            fontFamily = Jost,
                            fontSize = 11.sp,
                            color = if (seleccionada) Verde else TextoTerciario,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            // Descripción
            Text(
                text = "DESCRIPCIÓN",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = NumoColors.textoTerciario,
                letterSpacing = 0.8.sp
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { viewModel.onDescripcionCambiada(it) },
                placeholder = {
                    Text("Ej: Compra semanal", fontFamily = Jost, fontSize = 13.sp, color = NumoColors.textoTerciario)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor= NumoColors.verde,
                    unfocusedBorderColor = NumoColors.borde,
                    cursorColor= NumoColors.verde,
                    focusedTextColor = NumoColors.textoPrimario,
                    unfocusedTextColor = NumoColors.textoPrimario
                ),
                textStyle = TextStyle(fontFamily = Jost, fontSize = 13.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón guardar
            Button(
                onClick = { viewModel.guardarGasto() },
                enabled = categoriaSeleccionada != null && cantidad.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor= NumoColors.verde,
                    disabledContainerColor = NumoColors.borde
                )
            ) {
                Text(
                    text = "Guardar gasto",
                    fontFamily = Jost,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}