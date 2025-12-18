package com.example.myapplication43.presentation.viewmodel


import com.example.myapplication43.domain.models.Track

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentTrack: Track? = null,
    val currentPosition: Long = 0L, // <--- Текущее время (мс)
    val totalDuration: Long = 0L    // <--- Общая длина (мс)
)