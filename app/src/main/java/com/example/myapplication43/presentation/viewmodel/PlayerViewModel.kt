package com.example.myapplication43.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.presentation.player.MusicControllerImpl
import com.example.myapplication43.presentation.toMediaItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlayerViewModel(
    private val getHomeTracksUseCase: GetHomeTracksUseCase,
    private val musicController: MusicControllerImpl
) : ViewModel() {

    // 1. Стейт (Состояние)
    // Мы просто транслируем состояние из контроллера в UI.
    // UI подпишется на эту переменную и будет обновляться сам.
    val uiState: StateFlow<PlayerUiState> = musicController.playerState
    fun loadAndPlay() {
        // 1. Получаем данные из UseCase
        getHomeTracksUseCase() // Возвращает Flow
            .onEach { tracks ->
                // 2. Превращаем Tracks в MediaItems
                val mediaItems = tracks.map { it.toMediaItem() }

                // 3. Отдаем контроллеру
                musicController.playTrackList(mediaItems)
            }
            .launchIn(viewModelScope)
    }
    // 3. Метод Play/Pause
    // Логика простая: если играет -> пауза, иначе -> играть.
    fun togglePlayPause() {
        if (uiState.value.isPlaying) {
            musicController.pause()
        } else {
            // Если трек уже загружен, просто продолжаем
            if (uiState.value.currentTrack != null) {
                musicController.resume()
            } else {
                // Если ничего не загружено (старт приложения), грузим список
                loadAndPlay()
            }
        }
    }

    fun skipToNext() {
        musicController.skipToNext()
    }

    fun skipToPrevious() {
        musicController.skipToPrevious()
    }

    // Очистка ресурсов при уничтожении экрана не нужна здесь,
    // так как MusicController - синглтон (Single) в Koin и живет дольше экрана.
}