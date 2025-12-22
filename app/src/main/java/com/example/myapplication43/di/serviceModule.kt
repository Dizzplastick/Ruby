package com.example.myapplication43.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serviceModule = module {

    //Создаем ExoPlayer как синглтон
    single<ExoPlayer> {
        buildExoPlayer(androidContext())
    }
}

//Вспомогательная функция для настройки плеера
private fun buildExoPlayer(context: Context): ExoPlayer {
    return ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            true //ставить паузу, если позвонили)
        )
        .setHandleAudioBecomingNoisy(true) // Пауз если выдернули наушники
        .build()
}