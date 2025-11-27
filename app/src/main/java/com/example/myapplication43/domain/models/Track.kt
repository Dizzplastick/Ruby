package com.example.myapplication43.domain.models

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val mediaUrl: String, // link 4 track
    val coverUrl: String, // picture 4 track
    val isLiked: Boolean,


)