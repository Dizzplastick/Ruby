package com.example.myapplication43.service

import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import org.koin.android.ext.android.inject

class MusicService : MediaSessionService() {

    // Получаем наш плеер из Koin
    private val player: ExoPlayer by inject()

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        // Создаем сессию, связываем её с плеером
        mediaSession = MediaSession.Builder(this, player).build()
    }

    // Этот метод вызывается, когда кто-то (UI) хочет подключиться к сервису
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // Обязательно чистим ресурсы при уничтожении сервиса
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}