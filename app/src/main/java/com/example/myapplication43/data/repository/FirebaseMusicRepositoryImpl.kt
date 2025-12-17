package com.example.myapplication43.data.repository

import android.net.Uri
import com.example.myapplication43.domain.models.Track
import com.example.myapplication43.domain.repository.MusicRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseMusicRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage // <-- Добавили в конструктор
) : MusicRepository {

    // ... твои существующие методы getTracks и searchTracks оставляем без изменений ...
    override fun getTracks(): Flow<List<Track>> = callbackFlow {
        // ... твой старый код ...
        val listener = db.collection("tracks").addSnapshotListener { snapshot, error ->
            // ... (оставь как было в файле) ...
            if (snapshot != null) {
                val tracks = snapshot.documents.map { doc ->
                    Track(
                        id = doc.id,
                        title = doc.getString("title") ?: "Unknown",
                        artist = doc.getString("artist") ?: "Unknown",
                        mediaUri = doc.getString("mediaUri") ?: "",
                        coverUri = doc.getString("coverUri") ?: "",
                        isLiked = false
                    )
                }
                trySend(tracks)
            }
        }
        awaitClose { listener.remove() }
    }

    override fun searchTracks(query: String): Flow<List<Track>> = callbackFlow {
        // ... твой старый код ...
        awaitClose { } // заглушка, чтобы компилировалось, если код был не полным
    }

    override suspend fun toggleLike(trackId: String, isLiked: Boolean) { }

    // --- НОВЫЙ МЕТОД ---
    override suspend fun uploadTrack(title: String, artist: String, coverUri: Uri, audioUri: Uri): Boolean {
        return try {
            val trackId = UUID.randomUUID().toString()
            val storageRef = storage.reference

            // 1. Загружаем обложку
            val coverRef = storageRef.child("covers/$trackId.jpg")
            coverRef.putFile(coverUri).await()
            val downloadCoverUrl = coverRef.downloadUrl.await().toString()

            // 2. Загружаем аудио
            val audioRef = storageRef.child("audio/$trackId.mp3")
            audioRef.putFile(audioUri).await()
            val downloadAudioUrl = audioRef.downloadUrl.await().toString()

            // 3. Сохраняем данные в Firestore
            val track = Track(
                id = trackId,
                title = title,
                artist = artist,
                mediaUri = downloadAudioUrl,
                coverUri = downloadCoverUrl,
                isLiked = false
            )

            // Firebase сам преобразует объект Track в Map, если поля совпадают
            db.collection("tracks").document(trackId).set(track).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}