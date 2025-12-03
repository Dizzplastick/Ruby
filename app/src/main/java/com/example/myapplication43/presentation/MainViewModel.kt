package com.example.myapplication43.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.presentation.toMediaItem
import com.example.myapplication43.presentation.player.MusicControllerImpl
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val getHomeTracksUseCase: GetHomeTracksUseCase,
    private val musicController: MusicControllerImpl
) : ViewModel() {

    // Временный метод для теста, вызываем его из UI по кнопке
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

    override fun onCleared() {
        super.onCleared()
        musicController.release()
    }
}