package com.example.myapplication43.domain.models

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val mediaUri: String, // link 4 track
    val coverUri: String, // picture 4 track
    val isLiked: Boolean,


)