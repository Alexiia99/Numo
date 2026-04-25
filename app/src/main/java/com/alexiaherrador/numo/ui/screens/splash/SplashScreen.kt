package com.alexiaherrador.numo.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.airbnb.lottie.compose.*
import com.alexiaherrador.numo.ui.theme.Jost
import com.alexiaherrador.numo.ui.theme.NumoColors
import com.alexiaherrador.numo.ui.theme.PlayfairDisplay
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("splash_animation.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.2f
    )

    // Cuando termina la animación navega al siguiente destino
    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(300) // pequeña pausa al final
            onFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NumoColors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación Lottie
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(320.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre de la app
            Text(
                text = "Numo",
                fontFamily = PlayfairDisplay,
                fontSize = 40.sp,
                color = NumoColors.textoPrimario
            )

            Text(
                text = "Tus gastos, bajo control",
                fontFamily = Jost,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = NumoColors.textoTerciario
            )
        }
    }
}