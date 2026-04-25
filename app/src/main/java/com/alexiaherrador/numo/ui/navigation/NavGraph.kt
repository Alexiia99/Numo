package com.alexiaherrador.numo.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alexiaherrador.numo.data.local.AppDatabase
import com.alexiaherrador.numo.data.local.UserPreferences
import com.alexiaherrador.numo.data.repository.GastosRepository
import com.alexiaherrador.numo.ui.screens.ajustes.AjustesScreen
import com.alexiaherrador.numo.ui.screens.ajustes.AjustesViewModel
import com.alexiaherrador.numo.ui.screens.ajustes.AjustesViewModelFactory
import com.alexiaherrador.numo.ui.screens.anadir.AnadirGastoScreen
import com.alexiaherrador.numo.ui.screens.anadir.AnadirGastoViewModel
import com.alexiaherrador.numo.ui.screens.anadir.AnadirGastoViewModelFactory
import com.alexiaherrador.numo.ui.screens.dashboard.DashboardScreen
import com.alexiaherrador.numo.ui.screens.dashboard.DashboardViewModel
import com.alexiaherrador.numo.ui.screens.dashboard.DashboardViewModelFactory
import com.alexiaherrador.numo.ui.screens.estadisticas.EstadisticasScreen
import com.alexiaherrador.numo.ui.screens.estadisticas.EstadisticasViewModel
import com.alexiaherrador.numo.ui.screens.estadisticas.EstadisticasViewModelFactory
import com.alexiaherrador.numo.ui.screens.presupuestos.PresupuestosScreen
import com.alexiaherrador.numo.ui.screens.presupuestos.PresupuestosViewModel
import com.alexiaherrador.numo.ui.screens.presupuestos.PresupuestosViewModelFactory
import com.alexiaherrador.numo.ui.screens.recurrentes.RecurrentesScreen
import com.alexiaherrador.numo.ui.screens.recurrentes.RecurrentesViewModel
import com.alexiaherrador.numo.ui.screens.recurrentes.RecurrentesViewModelFactory
import com.alexiaherrador.numo.ui.screens.transacciones.TransaccionesScreen
import com.alexiaherrador.numo.ui.screens.transacciones.TransaccionesViewModel
import com.alexiaherrador.numo.ui.screens.transacciones.TransaccionesViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    context: Context
) {
    // Repository compartido por todas las pantallas
    val db = AppDatabase.getInstance(context)
    val repository = GastosRepository(db.gastoDao(), db.presupuestoDao(), db.gastoRecurrenteDao())

    NavHost(
        navController = navController,
        startDestination = Rutas.Dashboard.ruta,
        modifier = modifier
    ) {
        composable(Rutas.Dashboard.ruta) {
            val viewModel: DashboardViewModel = viewModel(
                factory = DashboardViewModelFactory(repository, UserPreferences(context))
            )
            DashboardScreen(
                viewModel = viewModel,
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Rutas.Transacciones.ruta) {
            val viewModel: TransaccionesViewModel = viewModel(
                factory = TransaccionesViewModelFactory(repository)
            )
            TransaccionesScreen(
                viewModel = viewModel,
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Rutas.AnadirGasto.ruta) {
            val viewModel: AnadirGastoViewModel = viewModel(
                factory = AnadirGastoViewModelFactory(repository)
            )
            AnadirGastoScreen(
                viewModel = viewModel,
                onGuardado = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(Rutas.Presupuestos.ruta) {
            val viewModel: PresupuestosViewModel = viewModel(
                factory = PresupuestosViewModelFactory(repository)
            )
            PresupuestosScreen(
                viewModel = viewModel,
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Rutas.Estadisticas.ruta) {
            val viewModel: EstadisticasViewModel = viewModel(
                factory = EstadisticasViewModelFactory(repository)
            )
            EstadisticasScreen(
                viewModel = viewModel,
                onNavigate = { navController.navigate(it) }
            )
        }
        composable(Rutas.Ajustes.ruta) {
            val viewModel: AjustesViewModel = viewModel(
                factory = AjustesViewModelFactory(UserPreferences(context), repository)
            )
            AjustesScreen(
                viewModel = viewModel,
                onNavigate = { navController.navigate(it) }
            )
        }

        composable(Rutas.Recurrentes.ruta) {
            val viewModel: RecurrentesViewModel = viewModel(
                factory = RecurrentesViewModelFactory(repository)
            )
            RecurrentesScreen(
                viewModel = viewModel,
                onNavigate = { navController.navigate(it) }
            )
        }
    }
}