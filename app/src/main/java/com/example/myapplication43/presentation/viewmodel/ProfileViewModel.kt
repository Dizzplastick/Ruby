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
import android.net.Uri
import kotlinx.coroutines.launch
import com.example.myapplication43.presentation.player.MusicControllerImpl
import com.example.myapplication43.data.toMediaItem

class ProfileViewModel(
    private val repository: MusicRepository,
    private val musicController: MusicControllerImpl,
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _userTracks = MutableStateFlow<List<Track>>(emptyList())
    val userTracks = _userTracks.asStateFlow()

    private val _likedTracks = MutableStateFlow<List<Track>>(emptyList())
    val likedTracks = _likedTracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()



    // Метод загрузки данных по ID
    fun loadProfileData(userIdArg: String) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        // Если пришло "me", используем свой ID, иначе - переданный
        val targetUid = if (userIdArg == "me") currentUid else userIdArg

        if (targetUid == null) return

        // 1. Загружаем инфо о юзере
        repository.getUserProfile(targetUid)
            .onEach { user -> _currentUser.value = user }
            .launchIn(viewModelScope)

        // 2. Загружаем его треки
        repository.getUserTracks(targetUid)
            .onEach { tracks -> _userTracks.value = tracks }
            .launchIn(viewModelScope)

        repository.getLikedTracks(targetUid).onEach { _likedTracks.value = it }.launchIn(viewModelScope)

    }

    // Проверка, мой ли это профиль (чтобы скрыть/показать кнопки редактирования)
    fun isMyProfile(userIdArg: String): Boolean {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        return userIdArg == "me" || userIdArg == currentUid
    }

    fun onTrackClick(track: Track, currentList: List<Track>) {
        // 1. Превращаем список треков (UserTracks или LikedTracks) в список для плеера
        val mediaItems = currentList.map { it.toMediaItem() }

        // 2. Ищем позицию нажатого трека
        val startIndex = currentList.indexOf(track).takeIf { it != -1 } ?: 0

        // 3. Отдаем команду контроллеру
        musicController.playTrackList(mediaItems, startIndex)
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

    // --- НОВЫЙ МЕТОД: Обновление профиля ---
    // 3. Обновление (только для своего профиля)
    fun updateProfile(newUsername: String, newAvatarUri: Uri?) {
        val user = _currentUser.value ?: return

        // Доп. защита: если ID юзера в профиле не совпадает с текущим авторизованным -> выход
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        if (user.id != currentUid) return

        viewModelScope.launch {
            _isLoading.value = true

            val finalAvatarUrl = if (newAvatarUri != null) {
                repository.uploadAvatar(user.id, newAvatarUri)
            } else {
                user.avatarUrl
            }

            val updatedUser = user.copy(username = newUsername, avatarUrl = finalAvatarUrl)
            repository.updateUser(updatedUser)
            _isLoading.value = false
        }
    }

    fun onSeek(position: Float) {
        // Slider возвращает Float, а плееру нужен Long
        musicController.seekTo(position.toLong())
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}