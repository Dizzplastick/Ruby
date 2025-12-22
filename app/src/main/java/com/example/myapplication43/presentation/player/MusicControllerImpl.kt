package com.example.myapplication43.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.presentation.viewmodel.PlayerUiState
import com.example.myapplication43.service.MusicService // Твой сервис
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class MusicControllerImpl(context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    //Создаем Scope для запуска таймера
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    //Безопасное получение контроллера получит как будет готов
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone && !mediaControllerFuture.isCancelled) {
            try { mediaControllerFuture.get() } catch (e: Exception) { null }
        } else null

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState = _playerState.asStateFlow()

    //Player.Listener інтерфейс від бібліотеки Media3, слідкуємо за плей пауз для перемальовки ui
    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState { it.copy(isPlaying = isPlaying) }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val track = mediaItem?.let {
                // Достаем данніе exoPlayer
                val extras = it.mediaMetadata.extras
                Track(
                    id = it.mediaId,
                    title = it.mediaMetadata.title.toString(),
                    artist = it.mediaMetadata.artist.toString(),
                    mediaUri = "",
                    coverUri = it.mediaMetadata.artworkUri.toString(),
                    isLiked = false,
                    userId = extras?.getString("userId") ?: "",
                    username = extras?.getString("username") ?: ""
                )
            }
            updateState { it.copy(currentTrack = track) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val controller = mediaController ?: return
            if (playbackState == Player.STATE_READY) {
                updateState { it.copy(totalDuration = controller.duration.coerceAtLeast(0L)) }}
        }
    }
        //соединяемся c MusicService
    init {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        // полсе готовности mediaController подписіваемся на действия плеера
        mediaControllerFuture.addListener({
            val controller = mediaController ?: return@addListener
            controller.addListener(playerListener)

            //Синхронизируем UI с текущим состоянием плеера при подключении
            if (controller.mediaItemCount > 0) {
                // Вручную дергаем обновление, чтобы UI узнал, что уже играет
                playerListener.onMediaItemTransition(controller.currentMediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
                playerListener.onIsPlayingChanged(controller.isPlaying)
                updateState { it.copy(totalDuration = controller.duration.coerceAtLeast(0L)) }
            }
        }, ContextCompat.getMainExecutor(context))

        startProgressUpdate()
    }

    private fun startProgressUpdate() {
        scope.launch {
            while (isActive) {
                val controller = mediaController
                //только если плеер реально играет
                if (controller != null && controller.isPlaying) {
                    _playerState.update { state ->
                        state.copy(
                            currentPosition = controller.currentPosition,
                            totalDuration = controller.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(1000L) // Ждем 1 секунду
            }
        }
    }

    //Перемотка
    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
        //Сразу обновляем UI
        _playerState.update { it.copy(currentPosition = position) }
    }

    fun playTrackList(mediaItems: List<MediaItem>, startIndex: Int = 0) {
        val controller = mediaController ?: return

        controller.setMediaItems(mediaItems, startIndex, 0L) // 0L - это позиция в мс (с начала трека)
        controller.prepare()
        controller.play()
    }

    //методы для переключения треков
    fun skipToNext() {
        mediaController?.seekToNext()
    }

    fun skipToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun resume() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun release() {
        //освобождаем ресурсы
        mediaController?.removeListener(playerListener)
        MediaController.releaseFuture(mediaControllerFuture)
        scope.cancel()
    }

    private fun updateState(function: (PlayerUiState) -> PlayerUiState) {
        _playerState.value = function(_playerState.value)
    }

    fun setLikeState(isLiked: Boolean) {
        val currentTrack = _playerState.value.currentTrack ?: return

        // Мы берем текущее состояние и подменяем в нем трек на такой же, но с новым лайком
        _playerState.value = _playerState.value.copy(
            currentTrack = currentTrack.copy(isLiked = isLiked)
        )
    }
}