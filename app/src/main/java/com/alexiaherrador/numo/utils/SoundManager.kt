package com.alexiaherrador.numo.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundId: Int = soundPool.load(
        context.assets.open("cash_register.mp3").let { input ->
            val tempFile = java.io.File(context.cacheDir, "cash_register.mp3")
            tempFile.outputStream().use { input.copyTo(it) }
            tempFile.absolutePath
        },
        1
    )

    fun reproducirCajaRegistradora() {
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }

    fun liberar() {
        soundPool.release()
    }
}