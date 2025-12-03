package com.example.myapplication43.data.repository

import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockMusicRepositoryImpl : MusicRepository {

    private val mockTracks = listOf(
        Track(
            "1",
            "Song A",
            "Artist A",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "url_img",
            false
        ),
        Track(
            "2",
            "Song B",
            "Artist B",
            "https://www.learningcontainer.com/wp-content/uploads/2020/02/Sample-OGG-File.ogg",
            "url_img",
            false
        )
    )

    override fun getTracks(): Flow<List<Track>> = flow {
        emit(mockTracks)
    }

    override fun searchTracks(query: String): Flow<List<Track>> = flow {

        emit(mockTracks.filter { it.title.contains(query, ignoreCase = true) })
    }

    override suspend fun toggleLike(trackId: String, isLiked: Boolean) {
        // Пока пусто, или меняем локальный список в памяти
    }
}