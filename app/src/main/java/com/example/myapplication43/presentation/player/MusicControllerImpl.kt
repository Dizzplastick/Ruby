package com.example.myapplication43.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.myapplication43.service.MusicService // Твой сервис
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors


class MusicControllerImpl(context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    init {
        // Настраиваем токен сессии для соединения с нашим MusicService
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        // Асинхронно подключаемся к сервису
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            // Контроллер готов к работе (здесь можно добавить логи)
        }, MoreExecutors.directExecutor())
    }

    // Метод для запуска списка треков
    fun playTrackList(mediaItems: List<MediaItem>) {
        val controller = mediaController ?: return // Если не подключились - выходим

        controller.setMediaItems(mediaItems)
        controller.prepare()
        controller.play()
    }

    // Очистка ресурсов (вызывать при закрытии приложения, если нужно)
    fun release() {
        MediaController.releaseFuture(mediaControllerFuture)
    }
}