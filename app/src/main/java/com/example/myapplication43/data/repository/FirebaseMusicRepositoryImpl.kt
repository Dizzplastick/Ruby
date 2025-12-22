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
import com.google.firebase.firestore.FieldValue

class FirebaseMusicRepositoryImpl(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MusicRepository {

    override fun getTracks(): Flow<List<Track>> = callbackFlow {
        val listener = db.collection("tracks").addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }

            if (snapshot != null) {
                val tracks = snapshot.documents.map { doc ->
                    Track(
                        id = doc.id,
                        title = doc.getString("title") ?: "Unknown",
                        artist = doc.getString("artist") ?: "Unknown",
                        mediaUri = doc.getString("mediaUri") ?: "",
                        coverUri = doc.getString("coverUri") ?: "",
                        isLiked = false,
                        userId = doc.getString("userId") ?: "",
                        username = doc.getString("username") ?: ""
                    )
                }
                trySend(tracks)
            }
        }
        awaitClose { listener.remove() }
    }

    override fun searchTracks(query: String): Flow<List<Track>> = callbackFlow {
        // Мы юзаем тот же слуштель что и для всех треков и фльтруем
        val listener = db.collection("tracks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                if (snapshot != null) {
                    val allTracks = snapshot.documents.map { doc ->
                        Track(
                            id = doc.id,
                            title = doc.getString("title") ?: "Unknown",
                            artist = doc.getString("artist") ?: "Unknown",
                            mediaUri = doc.getString("mediaUri") ?: "",
                            coverUri = doc.getString("coverUri") ?: "",
                            userId = doc.getString("userId") ?: "",
                            username = doc.getString("username") ?: "",
                            isLiked = false
                        )
                    }

                    //фильтрация ( если поле пустое ,то список не меняетс)
                    val filteredList = if (query.isBlank()) {
                        allTracks
                    } else {
                        allTracks.filter { track ->
                            track.title.contains(query, ignoreCase = true) ||
                                    track.artist.contains(query, ignoreCase = true)
                        }
                    }

                    trySend(filteredList)
                }
            }
        awaitClose { listener.remove() }
    }

    //ПЕРЕКЛЮЧЕНИЕ ЛАЙКА
    override suspend fun toggleLike(trackId: String, isLiked: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val trackRef = db.collection("users").document(uid)
            .collection("liked_tracks").document(trackId)

        if (isLiked) {
            //Если уже лайкнут дизлай
            trackRef.delete().await()
        } else {
            //Если не лайкнут нужно сохранить данные трека в liked_tracks
            val sourceTrackSnapshot = db.collection("tracks").document(trackId).get().await()
            val trackData = sourceTrackSnapshot.data

            if (trackData != null) {
                // добавляем поле timestamp, чтобы сортировать по дате добавления
                val dataToSave = trackData.toMutableMap()
                dataToSave["likedAt"] = FieldValue.serverTimestamp()
                trackRef.set(dataToSave).await()
            }
        }
    }

    //ПОЛУЧЕНИЕ ЛАЙКНУТЫХ ТРЕКОВ
    override fun getLikedTracks(userId: String): Flow<List<Track>> = callbackFlow {
        val listener = db.collection("users").document(userId)
            .collection("liked_tracks")
            .orderBy("likedAt", com.google.firebase.firestore.Query.Direction.DESCENDING) // Свежие сверху
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }

                if (snapshot != null) {
                    val tracks = snapshot.documents.map { doc ->
                        Track(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            artist = doc.getString("artist") ?: "",
                            mediaUri = doc.getString("mediaUri") ?: "",
                            coverUri = doc.getString("coverUri") ?: "",
                            userId = doc.getString("userId") ?: "",
                            username = doc.getString("username") ?: "",
                            isLiked = true // В этом списке они всегда лайкнуты
                        )
                    }
                    trySend(tracks)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun isTrackLiked(trackId: String): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return false
        try {
            val doc = db.collection("users").document(uid)
                .collection("liked_tracks").document(trackId)
                .get().await()
            return doc.exists() // Вернет true, если лайк есть
        } catch (e: Exception) {
            return false
        }
    }


    override suspend fun uploadTrack(
        title: String,
        artist: String,
        coverUri: Uri,
        audioUri: Uri
    ): Boolean {
        return try {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return false

            // 1. Получаем текущий никнейм юзера из базы
            val userSnapshot = db.collection("users").document(currentUserId).get().await()
            val currentUsername = userSnapshot.getString("username") ?: "Unknown"

            val trackId = UUID.randomUUID().toString()
            val storageRef = storage.reference

            // 2. Загружаем файл
            val coverRef = storageRef.child("covers/$trackId.jpg")
            coverRef.putFile(coverUri).await()
            val downloadCoverUrl = coverRef.downloadUrl.await().toString()

            val audioRef = storageRef.child("audio/$trackId.mp3")
            audioRef.putFile(audioUri).await()
            val downloadAudioUrl = audioRef.downloadUrl.await().toString()

            // 3. Создаем трек
            val track = Track(
                id = trackId,
                title = title,
                artist = artist,
                mediaUri = downloadAudioUrl,
                coverUri = downloadCoverUrl,
                userId = currentUserId,
                username = currentUsername, // <--- Сохраняем никнейм
                isLiked = false
            )
                //Сохранение в бд
            db.collection("tracks").document(trackId).set(track).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



override suspend fun saveUser(user: User) {
    //Сохраняем данные пользователя в users
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
    // Ищем треки где поле userId совпадает с переданным
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
        // Сохраняем аватарки в папку avatars
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
        // Перезаливам даные пользователя
        db.collection("users").document(user.id).set(user).await()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
}