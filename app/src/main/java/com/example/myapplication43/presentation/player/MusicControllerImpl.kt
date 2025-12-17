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
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MusicControllerImpl(context: Context) {

    private var mediaControllerFuture: ListenableFuture<MediaController>

    // Безопасное получение контроллера
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone && !mediaControllerFuture.isCancelled) {
            try { mediaControllerFuture.get() } catch (e: Exception) { null }
        } else null

    private val _playerState = MutableStateFlow(PlayerUiState())
    val playerState = _playerState.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState { it.copy(isPlaying = isPlaying) }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val track = mediaItem?.let {
                Track(
                    id = it.mediaId,
                    title = it.mediaMetadata.title.toString(),
                    artist = it.mediaMetadata.artist.toString(),
                    mediaUri = "",
                    coverUri = it.mediaMetadata.artworkUri.toString(),
                    isLiked = false
                )
            }
            updateState { it.copy(currentTrack = track) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            // Тут будем ловить окончание трека или буферизацию
        }
    }

    init {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        // Используем ContextCompat.getMainExecutor для безопасности потоков
        mediaControllerFuture.addListener({
            val controller = mediaController ?: return@addListener
            controller.addListener(playerListener)

            // Синхронизируем UI с текущим состоянием плеера при подключении
            if (controller.mediaItemCount > 0) {
                // Вручную дергаем обновление, чтобы UI узнал, что уже играет
                playerListener.onMediaItemTransition(controller.currentMediaItem, Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED)
                playerListener.onIsPlayingChanged(controller.isPlaying)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun playTrackList(mediaItems: List<MediaItem>, startIndex: Int = 0) {
        val controller = mediaController ?: return

        controller.setMediaItems(mediaItems, startIndex, 0L) // 0L - это позиция в мс (с начала трека)
        controller.prepare()
        controller.play()
    }

    // Добавим методы для переключения треков
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
        // Обязательно освобождаем ресурсы
        mediaController?.removeListener(playerListener)
        MediaController.releaseFuture(mediaControllerFuture)
    }

    private fun updateState(function: (PlayerUiState) -> PlayerUiState) {
        _playerState.value = function(_playerState.value)
    }
}