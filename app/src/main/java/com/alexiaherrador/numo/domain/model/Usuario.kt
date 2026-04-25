package com.alexiaherrador.numo.domain.model

data class Usuario(
    val nombre : String,
    val avatarEmoji : String = "👤",
    val fotoUri : String? = null,
    val monedaSimbolo : String = "€",
    val monedaNombre : String = "Euros",
    val diaIniciaMes : Int = 1,
    val presupuestoGlobal : Double = 0.0,
    val horaNotificacion : Int =21,
    val minutosNotificacion: Int = 30,
    val temaOscuro : Boolean = false,
    val colorAcento : String = "#2D5A3D"
    )