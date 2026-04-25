package com.alexiaherrador.numo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.alexiaherrador.numo.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión que crea el DataStore una sola vez
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "numo_preferencias")

class UserPreferences(private val context: Context) {

    // — Claves —
    companion object {
        val NOMBRE = stringPreferencesKey("nombre")
        val AVATAR_EMOJI = stringPreferencesKey("avatar_emoji")
        val FOTO_URI = stringPreferencesKey("foto_uri")
        val MONEDA_SIMBOLO = stringPreferencesKey("moneda_simbolo")
        val MONEDA_NOMBRE = stringPreferencesKey("moneda_nombre")
        val DIA_INICIA_MES = intPreferencesKey("dia_inicia_mes")
        val PRESUPUESTO_GLOBAL = floatPreferencesKey("presupuesto_global")
        val HORA_NOTIFICACION = intPreferencesKey("hora_notificacion")
        val MINUTOS_NOTIFICACION = intPreferencesKey("minutos_notificacion")
        val TEMA_OSCURO = booleanPreferencesKey("tema_oscuro")
        val COLOR_ACENTO = stringPreferencesKey("color_acento")
        val USUARIO_CREADO = booleanPreferencesKey("usuario_creado")
    }

    // — Leer usuario —
    val usuario: Flow<Usuario?> = context.dataStore.data.map { prefs ->
        val creado = prefs[USUARIO_CREADO] ?: false
        if (!creado) return@map null

        Usuario(
            nombre = prefs[NOMBRE] ?: "",
            avatarEmoji = prefs[AVATAR_EMOJI] ?: "👤",
            fotoUri = prefs[FOTO_URI],
            monedaSimbolo = prefs[MONEDA_SIMBOLO] ?: "€",
            monedaNombre = prefs[MONEDA_NOMBRE] ?: "Euros",
            diaIniciaMes = prefs[DIA_INICIA_MES] ?: 1,
            presupuestoGlobal = (prefs[PRESUPUESTO_GLOBAL] ?: 0f).toDouble(),
            horaNotificacion = prefs[HORA_NOTIFICACION] ?: 21,
            minutosNotificacion = prefs[MINUTOS_NOTIFICACION] ?: 30,
            temaOscuro = prefs[TEMA_OSCURO] ?: false,
            colorAcento = prefs[COLOR_ACENTO] ?: "#2D5A3D"
        )
    }

    val usuarioCreado: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[USUARIO_CREADO] ?: false
    }

    // — Guardar usuario —
    suspend fun guardarUsuario(usuario: Usuario) {
        context.dataStore.edit { prefs ->
            prefs[NOMBRE] = usuario.nombre
            prefs[AVATAR_EMOJI] = usuario.avatarEmoji
            usuario.fotoUri?.let { prefs[FOTO_URI] = it }
            prefs[MONEDA_SIMBOLO] = usuario.monedaSimbolo
            prefs[MONEDA_NOMBRE] = usuario.monedaNombre
            prefs[DIA_INICIA_MES] = usuario.diaIniciaMes
            prefs[PRESUPUESTO_GLOBAL] = usuario.presupuestoGlobal.toFloat()
            prefs[HORA_NOTIFICACION] = usuario.horaNotificacion
            prefs[MINUTOS_NOTIFICACION] = usuario.minutosNotificacion
            prefs[TEMA_OSCURO] = usuario.temaOscuro
            prefs[COLOR_ACENTO] = usuario.colorAcento
            prefs[USUARIO_CREADO] = true
        }
    }

    // — Borrar todos los datos —
    suspend fun borrarTodo() {
        context.dataStore.edit { it.clear() }
    }


}