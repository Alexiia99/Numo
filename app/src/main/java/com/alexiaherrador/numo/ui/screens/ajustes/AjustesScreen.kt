package com.alexiaherrador.numo.ui.screens.ajustes

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import java.io.File
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.alexiaherrador.numo.domain.model.Usuario
import com.alexiaherrador.numo.ui.theme.*

@Composable
fun AjustesScreen(
    viewModel: AjustesViewModel,
    onNavigate: (String) -> Unit
) {
    val usuario by viewModel.usuario.collectAsState()
    val guardadoExitoso by viewModel.guardadoExitoso.collectAsState()
    val context = LocalContext.current

    var mostrarDialogoBorrar by remember { mutableStateOf(false) }
    var mostrarDialogoNombre by remember { mutableStateOf(false) }
    var mostrarDialogoAvatar by remember { mutableStateOf(false) }
    var mostrarDialogoMoneda by remember { mutableStateOf(false) }
    var mostrarDialogoPresupuesto by remember { mutableStateOf(false) }
    var mostrarDialogoHora by remember { mutableStateOf(false) }
    var mostrarDialogoDia by remember { mutableStateOf(false) }

    val fotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { originalUri ->
            // Copia la imagen a almacenamiento privado de la app
            val archivoDestino = File(context.filesDir, "foto_perfil.jpg")
            context.contentResolver.openInputStream(originalUri)?.use { input ->
                archivoDestino.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            usuario?.let { user ->
                viewModel.guardarCambios(user.copy(fotoUri = archivoDestino.absolutePath))
            }
        }
    }

    LaunchedEffect(guardadoExitoso) {
        if (guardadoExitoso) viewModel.resetGuardado()
    }

    Scaffold(containerColor = NumoColors.background) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                CabeceraPerfil(
                    usuario = usuario,
                    onEditarNombre = { mostrarDialogoNombre = true },
                    onEditarAvatar = { mostrarDialogoAvatar = true }
                )
            }

            item {
                SeccionLabel(
                    texto = "General",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
            item {
                GrupoAjustes {
                    ItemAjuste(
                        icono = Icons.Rounded.ShoppingCart,
                        colorIcono = Color(0xFF2D9E5F),
                        titulo = "Moneda",
                        descripcion = "${usuario?.monedaSimbolo ?: "€"} · ${usuario?.monedaNombre ?: "Euros"}",
                        onClick = { mostrarDialogoMoneda = true }
                    )
                    HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
                    ItemAjuste(
                        icono = Icons.Rounded.DateRange,
                        colorIcono = Color(0xFFE8A838),
                        titulo = "Inicio del mes",
                        descripcion = "Día ${usuario?.diaIniciaMes ?: 1}",
                        onClick = { mostrarDialogoDia = true }
                    )
                    HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
                    ItemAjuste(
                        icono = Icons.Rounded.Warning,
                        colorIcono = Color(0xFF6B9BD2),
                        titulo = "Presupuesto global",
                        descripcion = "${String.format("%.0f", usuario?.presupuestoGlobal ?: 0.0)} ${NumoColors.moneda}/mes",
                        onClick = { mostrarDialogoPresupuesto = true }
                    )
                }
            }

            item {
                SeccionLabel(
                    texto = "Notificaciones",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
            item {
                GrupoAjustes {
                    ItemAjuste(
                        icono = Icons.Rounded.Notifications,
                        colorIcono = Color(0xFFC94F3A),
                        titulo = "Hora del recordatorio",
                        descripcion = "${usuario?.horaNotificacion ?: 21}:${
                            String.format("%02d", usuario?.minutosNotificacion ?: 30)
                        }",
                        onClick = { mostrarDialogoHora = true }
                    )
                    HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
                    ItemAjusteToggle(
                        icono = Icons.Rounded.Edit,
                        colorIcono = Color(0xFF6B6455),
                        titulo = "Tema oscuro",
                        descripcion = "Cambia el aspecto de la app",
                        activado = usuario?.temaOscuro ?: false,
                        onToggle = { activado ->
                            usuario?.let {
                                viewModel.guardarCambios(it.copy(temaOscuro = activado))
                            }
                        }
                    )
                }
            }

            item {
                SeccionLabel(
                    texto = "Datos",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
            item {
                GrupoAjustes {
                    ItemAjuste(
                        icono = Icons.Rounded.Email,
                        colorIcono = Color(0xFF2D5A3D),
                        titulo = "Exportar a CSV",
                        descripcion = "Descarga todos tus gastos",
                        onClick = { viewModel.exportarCSV(context) }
                    )
                    HorizontalDivider(color = NumoColors.borde, thickness = 0.5.dp)
                    ItemAjuste(
                        icono = Icons.Rounded.Email,
                        colorIcono = Color(0xFFC94F3A),
                        titulo = "Exportar a PDF",
                        descripcion = "Informe mensual en PDF",
                        onClick = { viewModel.exportarPDF(context) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFDECEA))
                        .clickable { mostrarDialogoBorrar = true }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = null,
                        tint = Color(0xFFC94F3A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Borrar todos los datos",
                        fontFamily = Jost,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color(0xFFC94F3A)
                    )
                }
            }
        }
    }

    if (mostrarDialogoBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrar = false },
            containerColor = NumoColors.surface,
            title = { Text("¿Borrar todo?", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                Text(
                    "Se eliminarán todos tus gastos, presupuestos y configuración. Esta acción no se puede deshacer.",
                    fontFamily = Jost, fontSize = 13.sp, color = NumoColors.textoSecundario
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.borrarTodo()
                    mostrarDialogoBorrar = false
                }) {
                    Text("Borrar", color = Color(0xFFC94F3A), fontFamily = Jost, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoBorrar = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }

    if (mostrarDialogoNombre) {
        var nuevoNombre by remember { mutableStateOf(usuario?.nombre ?: "") }
        AlertDialog(
            onDismissRequest = { mostrarDialogoNombre = false },
            containerColor = NumoColors.surface,
            title = { Text("Tu nombre", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { nuevoNombre = it },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NumoColors.verde,
                        unfocusedBorderColor = NumoColors.borde,
                        cursorColor = NumoColors.verde
                    ),
                    textStyle = TextStyle(fontFamily = Jost, color = NumoColors.textoPrimario),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        usuario?.let { viewModel.guardarCambios(it.copy(nombre = nuevoNombre.trim())) }
                        mostrarDialogoNombre = false
                    },
                    enabled = nuevoNombre.isNotBlank()
                ) {
                    Text("Guardar", fontFamily = Jost, fontWeight = FontWeight.SemiBold, color = NumoColors.verde)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoNombre = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }

    if (mostrarDialogoAvatar) {
        AlertDialog(
            containerColor = NumoColors.surface,
            onDismissRequest = { mostrarDialogoAvatar = false },
            title = { Text("Tu avatar", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NumoColors.surface2)
                            .clickable {
                                fotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                                mostrarDialogoAvatar = false
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📷", fontSize = 20.sp)
                        Text("Elegir foto del móvil", fontFamily = Jost, fontSize = 13.sp, color = NumoColors.textoPrimario)
                    }

                    Text("O elige un emoji:", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.textoTerciario)

                    var avatarTemp by remember { mutableStateOf(usuario?.avatarEmoji ?: "👤") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("👤", "👩", "👨", "👧", "👦", "🧑", "👩‍💼", "👨‍💼").forEach { emoji ->
                            val seleccionado = emoji == avatarTemp
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (seleccionado) NumoColors.verde.copy(alpha = 0.15f) else NumoColors.surface2,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        width = if (seleccionado) 2.dp else 1.dp,
                                        color = if (seleccionado) NumoColors.verde else NumoColors.borde,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        usuario?.let {
                                            viewModel.guardarCambios(it.copy(avatarEmoji = emoji, fotoUri = null))
                                        }
                                        mostrarDialogoAvatar = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAvatar = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }

    if (mostrarDialogoMoneda) {
        val monedas = listOf(
            Pair("€", "Euros"),
            Pair("$", "Dólares"),
            Pair("£", "Libras"),
            Pair("¥", "Yenes"),
            Pair("CHF", "Francos suizos")
        )
        AlertDialog(
            onDismissRequest = { mostrarDialogoMoneda = false },
            containerColor = NumoColors.surface,
            title = { Text("Moneda", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    monedas.forEach { (simbolo, nombre) ->
                        val seleccionada = usuario?.monedaSimbolo == simbolo
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (seleccionada) NumoColors.verde.copy(alpha = 0.12f) else Color.Transparent)
                                .clickable {
                                    usuario?.let {
                                        viewModel.guardarCambios(
                                            it.copy(monedaSimbolo = simbolo, monedaNombre = nombre)
                                        )
                                    }
                                    mostrarDialogoMoneda = false
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("$simbolo  $nombre", fontFamily = Jost, fontSize = 13.sp, color = NumoColors.textoPrimario)
                            if (seleccionada) {
                                Text("✓", color = NumoColors.verde, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { mostrarDialogoMoneda = false }) {
                    Text("Cerrar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }

    if (mostrarDialogoPresupuesto) {
        var presupuestoInput by remember {
            mutableStateOf(
                usuario?.presupuestoGlobal?.let {
                    if (it > 0) String.format("%.0f", it) else ""
                } ?: ""
            )
        }
        AlertDialog(
            onDismissRequest = { mostrarDialogoPresupuesto = false },
            containerColor = NumoColors.surface,
            title = { Text("Presupuesto global", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                OutlinedTextField(
                    value = presupuestoInput,
                    onValueChange = { presupuestoInput = it },
                    placeholder = { Text("0 ${NumoColors.moneda}/mes", fontFamily = Jost, color = NumoColors.textoTerciario) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NumoColors.verde,
                        unfocusedBorderColor = NumoColors.borde,
                        cursorColor = NumoColors.verde
                    ),
                    textStyle = TextStyle(fontFamily = Jost, color = NumoColors.textoPrimario),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val valor = presupuestoInput.toDoubleOrNull() ?: 0.0
                        usuario?.let { viewModel.guardarCambios(it.copy(presupuestoGlobal = valor)) }
                        mostrarDialogoPresupuesto = false
                    },
                    enabled = presupuestoInput.isNotBlank()
                ) {
                    Text("Guardar", fontFamily = Jost, fontWeight = FontWeight.SemiBold, color = NumoColors.verde)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoPresupuesto = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }

    if (mostrarDialogoHora) {
        var horaInput by remember { mutableStateOf(usuario?.horaNotificacion?.toString() ?: "21") }
        var minutosInput by remember { mutableStateOf(String.format("%02d", usuario?.minutosNotificacion ?: 30)) }
        AlertDialog(
            onDismissRequest = { mostrarDialogoHora = false },
            containerColor = NumoColors.surface,
            title = { Text("Hora del recordatorio", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = horaInput,
                        onValueChange = { if (it.length <= 2) horaInput = it },
                        label = { Text("Hora", fontFamily = Jost, fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NumoColors.verde,
                            unfocusedBorderColor = NumoColors.borde,
                            cursorColor = NumoColors.verde
                        ),
                        textStyle = TextStyle(fontFamily = Jost, color = NumoColors.textoPrimario),
                        modifier = Modifier.weight(1f)
                    )
                    Text(":", fontFamily = PlayfairDisplay, fontSize = 24.sp, color = NumoColors.textoPrimario)
                    OutlinedTextField(
                        value = minutosInput,
                        onValueChange = { if (it.length <= 2) minutosInput = it },
                        label = { Text("Min", fontFamily = Jost, fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NumoColors.verde,
                            unfocusedBorderColor = NumoColors.borde,
                            cursorColor = NumoColors.verde
                        ),
                        textStyle = TextStyle(fontFamily = Jost, color = NumoColors.textoPrimario),
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val hora = horaInput.toIntOrNull()?.coerceIn(0, 23) ?: 21
                    val minutos = minutosInput.toIntOrNull()?.coerceIn(0, 59) ?: 30
                    usuario?.let {
                        viewModel.guardarCambios(
                            it.copy(horaNotificacion = hora, minutosNotificacion = minutos),
                            context
                        )
                    }
                    mostrarDialogoHora = false
                }) {
                    Text("Guardar", fontFamily = Jost, fontWeight = FontWeight.SemiBold, color = NumoColors.verde)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoHora = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }

    if (mostrarDialogoDia) {
        var diaInput by remember { mutableStateOf(usuario?.diaIniciaMes?.toString() ?: "1") }
        AlertDialog(
            onDismissRequest = { mostrarDialogoDia = false },
            containerColor = NumoColors.surface,
            title = { Text("Día de inicio del mes", fontFamily = PlayfairDisplay, fontSize = 20.sp, color = NumoColors.textoPrimario) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Elige el día en que empieza tu ciclo mensual",
                        fontFamily = Jost,
                        fontSize = 12.sp,
                        color = NumoColors.textoSecundario
                    )
                    OutlinedTextField(
                        value = diaInput,
                        onValueChange = { if (it.length <= 2) diaInput = it },
                        placeholder = { Text("1-31", fontFamily = Jost, color = NumoColors.textoTerciario) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NumoColors.verde,
                            unfocusedBorderColor = NumoColors.borde,
                            cursorColor = NumoColors.verde
                        ),
                        textStyle = TextStyle(fontFamily = Jost, color = NumoColors.textoPrimario),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dia = diaInput.toIntOrNull()?.coerceIn(1, 31) ?: 1
                        usuario?.let { viewModel.guardarCambios(it.copy(diaIniciaMes = dia)) }
                        mostrarDialogoDia = false
                    },
                    enabled = diaInput.isNotBlank()
                ) {
                    Text("Guardar", fontFamily = Jost, fontWeight = FontWeight.SemiBold, color = NumoColors.verde)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoDia = false }) {
                    Text("Cancelar", fontFamily = Jost, color = NumoColors.textoTerciario)
                }
            }
        )
    }
}

@Composable
private fun CabeceraPerfil(
    usuario: Usuario?,
    onEditarNombre: () -> Unit,
    onEditarAvatar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NumoColors.verde)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (usuario?.fotoUri != null) {
            AsyncImage(
                model = usuario.fotoUri,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable { onEditarAvatar() },
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(NumoColors.sobreVerdeContainer)
                    .clickable { onEditarAvatar() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = usuario?.avatarEmoji ?: "👤", fontSize = 28.sp)
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = usuario?.nombre ?: "Usuario",
                fontFamily = Jost,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = NumoColors.sobreVerde
            )
            Text(
                text = "Toca el avatar para cambiarlo",
                fontFamily = Jost,
                fontSize = 11.sp,
                color =  NumoColors.sobreVerdeSubtitle
            )
        }
        TextButton(onClick = onEditarNombre) {
            Text("Editar", fontFamily = Jost, fontSize = 11.sp, color = NumoColors.sobreVerdeSubtitle)
        }
    }
}

@Composable
private fun SeccionLabel(texto: String, modifier: Modifier = Modifier) {
    Text(
        text = texto.uppercase(),
        fontFamily = Jost,
        fontWeight = FontWeight.SemiBold,
        fontSize = 9.sp,
        color = NumoColors.textoTerciario,
        letterSpacing = 0.8.sp,
        modifier = modifier
    )
}

@Composable
private fun GrupoAjustes(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NumoColors.surface)
    ) {
        content()
    }
}

@Composable
private fun ItemAjuste(
    icono: ImageVector,
    colorIcono: Color,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorIcono.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = NumoColors.textoPrimario)
            Text(descripcion, fontFamily = Jost, fontSize = 10.sp, color = NumoColors.textoTerciario)
        }
        Icon(imageVector = Icons.Rounded.Build, contentDescription = null, tint = NumoColors.textoTerciario, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun ItemAjusteToggle(
    icono: ImageVector,
    colorIcono: Color,
    titulo: String,
    descripcion: String,
    activado: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorIcono.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, fontFamily = Jost, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = NumoColors.textoPrimario)
            Text(descripcion, fontFamily = Jost, fontSize = 10.sp, color = NumoColors.textoTerciario)
        }
        Switch(
            checked = activado,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NumoColors.verde
            )
        )
    }
}