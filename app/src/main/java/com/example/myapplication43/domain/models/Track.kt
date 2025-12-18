package com.example.myapplication43.domain.models

data class Track(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val mediaUri: String = "",
    val coverUri: String = "",
    val userId: String = "",
    val username: String = "", // <--- Добавили поле
    val isLiked: Boolean = false
)