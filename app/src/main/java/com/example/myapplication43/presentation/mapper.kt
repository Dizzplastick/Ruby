package com.example.myapplication43.presentation

import android.net.Uri
import android.os.Bundle // <--- Добавь импорт
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.myapplication43.domain.models.Track

fun Track.toMediaItem(): MediaItem {
    // Упаковываем кастомные данные
    val extras = Bundle().apply {
        putString("userId", userId)
        putString("username", username)
    }

    return MediaItem.Builder()
        .setUri(mediaUri)
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setArtworkUri(Uri.parse(coverUri))
                .setExtras(extras) // <--- ВАЖНО: Кладем данные в рюкзак
                .build()
        )
        .build()
}