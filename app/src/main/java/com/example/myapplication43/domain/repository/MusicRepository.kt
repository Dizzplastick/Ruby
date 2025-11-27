package com.example.myapplication43.domain.repository

import com.example.myapplication43.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {

    fun getTraks(): Flow<List<Track>>

    fun searchTrack(query: String): Flow<List<Track>>

    fun toggleLike(trackId: String , isLiked: Boolean)

}