package com.example.myapplication43.presentation.viewmodel


import com.example.myapplication43.domain.models.Track

data class PlayerUiState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L
)