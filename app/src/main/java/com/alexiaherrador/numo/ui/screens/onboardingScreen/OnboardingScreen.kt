package com.alexiaherrador.numo.ui.screens.onboardingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexiaherrador.numo.ui.theme.*

@Composable
fun OnboardingScreen(
    onUsuarioCreado: (nombre: String, avatar: String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var avatarSeleccionado by remember { mutableStateOf("👤") }

    val avatares = listOf("👤", "👩", "👨", "👧", "👦", "🧑", "👩‍💼", "👨‍💼")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Crema)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo / emoji app
            Text(
                text = "",
                fontSize = 64.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            Text(
                text = "Hola, soy Numo",
                fontFamily = PlayfairDisplay,
                fontSize = 32.sp,
                color = NumoColors.textoPrimario,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtítulo
            Text(
                text = "Tu app de gastos personales.\n¿Cómo te llamas?",
                fontFamily = Jost,
                fontSize = 14.sp,
                color = NumoColors.textoSecundario,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = {
                    Text(
                        "Tu nombre",
                        fontFamily = Jost,
                        color = NumoColors.textoTerciario
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor= NumoColors.verde,
                    unfocusedBorderColor = NumoColors.borde,
                    focusedLabelColor= NumoColors.verde,
                    cursorColor= NumoColors.verde
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Jost,
                    fontSize = 16.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Selector avatar
            Text(
                text = "Elige tu avatar",
                fontFamily = Jost,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = NumoColors.textoSecundario,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                avatares.forEach { emoji ->
                    val seleccionado = emoji == avatarSeleccionado
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (seleccionado) Color(0xFFEEF5F0) else CremaSuave,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = if (seleccionado) 2.dp else 1.dp,
                                color = if (seleccionado) Verde else Borde,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { avatarSeleccionado = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 22.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón empezar
            Button(
                onClick = { onUsuarioCreado(nombre.trim(), avatarSeleccionado) },
                enabled = nombre.isNotBlank(),
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
                    text = "Empezar",
                    fontFamily = Jost,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}