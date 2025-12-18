package com.example.myapplication43.domain.useCase


import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class SearchTracksUseCase(private val repository: MusicRepository) {
    operator fun invoke(query: String): Flow<List<Track>> {
        return repository.searchTracks(query)
    }
}