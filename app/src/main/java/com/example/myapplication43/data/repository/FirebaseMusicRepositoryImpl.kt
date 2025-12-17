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
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication43.domain.models.User


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
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return false // Проверка
            val trackId = UUID.randomUUID().toString()
            val storageRef = storage.reference

            // Загрузка файлов... (как и было)
            val coverRef = storageRef.child("covers/$trackId.jpg")
            coverRef.putFile(coverUri).await()
            val downloadCoverUrl = coverRef.downloadUrl.await().toString()

            val audioRef = storageRef.child("audio/$trackId.mp3")
            audioRef.putFile(audioUri).await()
            val downloadAudioUrl = audioRef.downloadUrl.await().toString()

            // Сохраняем с userId
            val track = Track(
                id = trackId,
                title = title,
                artist = artist,
                mediaUri = downloadAudioUrl,
                coverUri = downloadCoverUrl,
                userId = currentUserId, // <--- ВАЖНО: Привязываем трек к юзеру
                isLiked = false
            )

            db.collection("tracks").document(trackId).set(track).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // --- НОВЫЕ МЕТОДЫ ---

    override suspend fun saveUser(user: User) {
        // Сохраняем данные пользователя в коллекцию "users"
        try {
            db.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getUserProfile(userId: String): Flow<User?> = callbackFlow {
        val listener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, _ ->
                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }
        awaitClose { listener.remove() }
    }

    override fun getUserTracks(userId: String): Flow<List<Track>> = callbackFlow {
        // Ищем треки, где поле userId совпадает с переданным
        val listener = db.collection("tracks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val tracks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Track::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(tracks)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun uploadAvatar(uid: String, uri: Uri): String {
        return try {
            // Сохраняем аватарки в папку avatars/uid.jpg
            val storageRef = storage.reference.child("avatars/$uid.jpg")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun updateUser(user: User) {
        try {
            // Перезаписываем данные пользователя
            db.collection("users").document(user.id).set(user).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}