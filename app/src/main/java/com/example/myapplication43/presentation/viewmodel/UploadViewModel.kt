package com.example.myapplication43.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication43.domain.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UploadViewModel(
    private val repository: MusicRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uploadSuccess = MutableStateFlow<Boolean?>(null)
    val uploadSuccess = _uploadSuccess.asStateFlow()

    fun uploadTrack(title: String, artist: String, coverUri: Uri?, audioUri: Uri?) {
        if (title.isBlank() || artist.isBlank() || coverUri == null || audioUri == null) return

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.uploadTrack(title, artist, coverUri, audioUri)
            _isLoading.value = false
            _uploadSuccess.value = result
        }
    }

    fun resetState() {
        _uploadSuccess.value = null
    }
}