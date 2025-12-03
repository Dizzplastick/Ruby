package com.example.myapplication43.presentation

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import android.net.Uri
import com.example.myapplication43.domain.models.Track

fun Track.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setUri(mediaUri)
        .setMediaId(id)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setArtworkUri(Uri.parse(coverUri))
                .build()
        )
        .build()
}