package com.example.myapplication43.domain.repository

import com.example.myapplication43.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getTracks(): Flow<List<Track>>

    fun searchTracks(query: String): Flow<List<Track>>

    suspend fun toggleLike (trackId: String , isLiked: Boolean)

}