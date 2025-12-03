package com.example.myapplication43.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serviceModule = module {

    // 1. Создаем ExoPlayer как Singleton
    single<ExoPlayer> {
        buildExoPlayer(androidContext())
    }
}

// Вспомогательная функция для настройки плеера
private fun buildExoPlayer(context: Context): ExoPlayer {
    return ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true // handleAudioFocus = true (ставить паузу, если позвонили)
        )
        .setHandleAudioBecomingNoisy(true) // Пауза, если выдернули наушники
        .build()
}