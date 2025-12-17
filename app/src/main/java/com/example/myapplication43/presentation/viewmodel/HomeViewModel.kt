package com.example.myapplication43.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.presentation.toMediaItem
import com.example.myapplication43.presentation.player.MusicControllerImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val getHomeTracksUseCase: GetHomeTracksUseCase,
    private val musicController: MusicControllerImpl
) : ViewModel() {

    // Состояние списка треков (изначально пустой)
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks = _tracks.asStateFlow()

    // Текст поискового запроса
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        loadTracks()
    }

    private fun loadTracks() {
        getHomeTracksUseCase()
            .onEach { list ->
                _tracks.value = list
            }
            .launchIn(viewModelScope)
    }

    // Когда пользователь кликает на трек
    fun onTrackClick(track: Track) {
        val currentList = _tracks.value
        val mediaItems = currentList.map { it.toMediaItem() }

        // Находим индекс нажатого трека в списке
        val startIndex = currentList.indexOf(track).takeIf { it != -1 } ?: 0

        // Передаем индекс в контроллер
        musicController.playTrackList(mediaItems, startIndex)
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        // Здесь позже подключим UseCase поиска
    }
}