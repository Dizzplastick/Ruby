package com.example.myapplication43.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.repository.MusicRepository
import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.presentation.player.MusicControllerImpl
import com.example.myapplication43.presentation.toMediaItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val getHomeTracksUseCase: GetHomeTracksUseCase,
    private val musicController: MusicControllerImpl,
    private val repository: MusicRepository
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


    fun onLikeClick() {
        // 1. Берем текущий трек и его состояние
        val track = uiState.value.currentTrack ?: return
        val currentLikeState = track.isLiked

        // 2. Вычисляем новое состояние (инверсия)
        val newLikeState = !currentLikeState

        // 3. МГНОВЕННО обновляем UI через контроллер
        musicController.setLikeState(newLikeState)

        // 4. Отправляем запрос в базу (фоном)
        viewModelScope.launch {
            repository.toggleLike(track.id, currentLikeState)
            // Обрати внимание: toggleLike в репозитории ждет "старое" состояние
            // или "новое" зависит от твоей реализации.
            // Судя по твоему коду репозитория выше:
            // if (isLiked) -> удаляет (дизлайк)
            // else -> добавляет (лайк)
            // Значит, передаем ТЕКУЩЕЕ (old) состояние, чтобы он понял, что делать.
        }
    }
}