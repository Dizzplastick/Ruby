package com.example.myapplication43.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.useCase.GetHomeTracksUseCase
import com.example.myapplication43.domain.useCase.SearchTracksUseCase
import com.example.myapplication43.presentation.toMediaItem
import com.example.myapplication43.presentation.player.MusicControllerImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HomeViewModel(
    private val getHomeTracksUseCase: GetHomeTracksUseCase,
    private val searchTracksUseCase: SearchTracksUseCase,
    private val musicController: MusicControllerImpl
) : ViewModel() {

    // Состояние списка треков (изначально пустой)
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks = _tracks.asStateFlow()

    // Текст поискового запроса
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Храним текущую работу по загрузке, чтобы отменять предыдущую при быстром вводе
    private var searchJob: Job? = null

    init {
        loadTracks()
    }

    // Загрузка всех треков (без фильтра)
    private fun loadTracks() {
        searchJob?.cancel() // Отменяем текущий поиск, если был
        searchJob = getHomeTracksUseCase()
            .onEach { list -> _tracks.value = list }
            .launchIn(viewModelScope)
    }

    // Загрузка с поиском
    private fun performSearch(query: String) {
        searchJob?.cancel() // Отменяем предыдущий запрос
        searchJob = searchTracksUseCase(query)
            .onEach { list -> _tracks.value = list }
            .launchIn(viewModelScope)
    }

    // Метод вызывается каждый раз, когда меняется буква в поле ввода
    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery

        if (newQuery.isBlank()) {
            loadTracks() // Если стерли текст -> показываем всё
        } else {
            performSearch(newQuery) // Иначе -> ищем
        }
    }
    fun onTrackClick(track: Track) {
        val currentList = _tracks.value
        // Превращаем наши Tracks в MediaItems для ExoPlayer
        val mediaItems = currentList.map { it.toMediaItem() }

        // Находим позицию трека, чтобы начать воспроизведение именно с него
        val startIndex = currentList.indexOf(track).takeIf { it != -1 } ?: 0

        musicController.playTrackList(mediaItems, startIndex)
    }
}