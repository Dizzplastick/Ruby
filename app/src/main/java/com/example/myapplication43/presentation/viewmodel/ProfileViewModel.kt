package com.example.myapplication43.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.models.User
import com.example.myapplication43.domain.repository.MusicRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _userTracks = MutableStateFlow<List<Track>>(emptyList())
    val userTracks = _userTracks.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // 1. Загружаем инфо о юзере
        repository.getUserProfile(uid)
            .onEach { user -> _currentUser.value = user }
            .launchIn(viewModelScope)

        // 2. Загружаем его треки
        repository.getUserTracks(uid)
            .onEach { tracks -> _userTracks.value = tracks }
            .launchIn(viewModelScope)
    }
}