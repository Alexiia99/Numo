package com.alexiaherrador.numo.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alexiaherrador.numo.data.local.UserPreferences
import com.alexiaherrador.numo.data.repository.GastosRepository

class DashboardViewModelFactory(
    private val repository: GastosRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}