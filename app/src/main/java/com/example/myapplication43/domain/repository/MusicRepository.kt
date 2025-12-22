package com.example.myapplication43.domain.repository

import com.example.myapplication43.domain.models.Track
import kotlinx.coroutines.flow.Flow
import android.net.Uri
import com.example.myapplication43.domain.models.User

interface MusicRepository {

    fun getTracks(): Flow<List<Track>>

    fun searchTracks(query: String): Flow<List<Track>>

    suspend fun toggleLike (trackId: String , isLiked: Boolean)

    fun getLikedTracks(userId: String): Flow<List<Track>>

    suspend fun isTrackLiked(trackId: String): Boolean

    suspend fun uploadTrack(title: String, artist: String, coverUri: Uri, audioUri: Uri): Boolean

    suspend fun saveUser(user: User) // при регистрации
    fun getUserProfile(userId: String): Flow<User?>
    fun getUserTracks(userId: String): Flow<List<Track>>

    suspend fun updateUser(user: User)
    suspend fun uploadAvatar(uid: String, uri: Uri): String
}