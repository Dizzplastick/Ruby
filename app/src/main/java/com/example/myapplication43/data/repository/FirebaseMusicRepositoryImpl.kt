package com.example.myapplication43.data.repository

import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.repository.MusicRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseMusicRepositoryImpl(
    private val db: FirebaseFirestore
) : MusicRepository {

    override fun getTracks(): Flow<List<Track>> = callbackFlow {
        // Слушаем коллекцию "tracks" в реальном времени
        val listener = db.collection("tracks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Если ошибка - закрываем поток
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Превращаем документы Firebase в наши объекты Track
                    val tracks = snapshot.documents.map { doc ->
                        Track(
                            id = doc.id, // ID документа становится ID трека
                            title = doc.getString("title") ?: "Unknown",
                            artist = doc.getString("artist") ?: "Unknown",
                            mediaUri = doc.getString("mediaUri") ?: "",
                            coverUri = doc.getString("coverUri") ?: "",
                            isLiked = false // Пока лайки не храним в этой коллекции
                        )
                    }
                    trySend(tracks) // Отправляем список в приложение
                }
            }

        // Когда поток закрывается (пользователь ушел с экрана), отписываемся от базы
        awaitClose { listener.remove() }
    }

    override fun searchTracks(query: String): Flow<List<Track>> = callbackFlow {
        // Простой поиск на клиенте (для начала)
        // В продакшене лучше использовать Algolia или специальные запросы Firestore
        val listener = db.collection("tracks")
            .addSnapshotListener { snapshot, _ ->
                val allTracks = snapshot?.documents?.map { doc ->
                    Track(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        artist = doc.getString("artist") ?: "",
                        mediaUri = doc.getString("mediaUri") ?: "",
                        coverUri = doc.getString("coverUri") ?: "",
                        isLiked = false
                    )
                } ?: emptyList()

                // Фильтруем
                val filtered = allTracks.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.artist.contains(query, ignoreCase = true)
                }
                trySend(filtered)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun toggleLike(trackId: String, isLiked: Boolean) {
        // Реализуем позже, когда подключим Auth
    }
}