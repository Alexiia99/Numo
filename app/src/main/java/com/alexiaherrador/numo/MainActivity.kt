package com.alexiaherrador.numo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexiaherrador.numo.data.local.UserPreferences
import com.alexiaherrador.numo.domain.model.Usuario
import com.alexiaherrador.numo.notifications.NotificacionWorker
import com.alexiaherrador.numo.ui.components.BottomNavBar
import com.alexiaherrador.numo.ui.navigation.NavGraph
import com.alexiaherrador.numo.ui.navigation.Rutas
import com.alexiaherrador.numo.ui.screens.onboardingScreen.OnboardingScreen
import com.alexiaherrador.numo.ui.screens.splash.SplashScreen
import com.alexiaherrador.numo.ui.theme.NumoColors
import com.alexiaherrador.numo.ui.theme.NumoTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferences = UserPreferences(applicationContext)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val usuario by userPreferences.usuario.collectAsState(initial = null)
            val usuarioCreado by userPreferences.usuarioCreado.collectAsState(initial = false)
            var mostrarSplash by remember { mutableStateOf(true) }

            NumoColors.isDark = usuario?.temaOscuro ?: false
            NumoColors.moneda = usuario?.monedaSimbolo ?: "€"

            NumoTheme(darkTheme = usuario?.temaOscuro ?: false) {
                if (mostrarSplash) {
                    SplashScreen(onFinished = { mostrarSplash = false })
                } else {
                    when {
                        !usuarioCreado -> {
                            OnboardingScreen(
                                onUsuarioCreado = { nombre, avatar ->
                                    lifecycleScope.launch {
                                        userPreferences.guardarUsuario(
                                            Usuario(
                                                nombre = nombre,
                                                avatarEmoji = avatar
                                            )
                                        )
                                    }
                                }
                            )
                        }

                        usuario != null -> {
                            val navController = rememberNavController()
                            val backStackEntry by navController.currentBackStackEntryAsState()
                            val rutaActual = backStackEntry?.destination?.route
                            val mostrarNavBar = rutaActual != Rutas.AnadirGasto.ruta

                            Scaffold(
                                containerColor = NumoColors.background,
                                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                                bottomBar = {
                                    if (mostrarNavBar) {
                                        BottomNavBar(
                                            rutaActual = rutaActual,
                                            onNavegar = { navController.navigate(it) }
                                        )
                                    }
                                }
                            ) { paddingValues ->
                                NavGraph(
                                    navController = navController,
                                    modifier = Modifier.padding(paddingValues),
                                    context = applicationContext
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "numo_descargas",
                "Descargas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de exportación de datos"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        programarNotificacionDiaria()
    }

    private fun programarNotificacionDiaria() {
        lifecycleScope.launch {
            val prefs = UserPreferences(this@MainActivity)
            val usuario = prefs.usuario.first()
            val hora = usuario?.horaNotificacion ?: 21
            val minutos = usuario?.minutosNotificacion ?: 30
            NotificacionWorker.programar(this@MainActivity, hora, minutos)
        }
    }
}