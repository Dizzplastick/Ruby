package com.example.myapplication43.domain.models


data class User(
    val id: String = "",
    val email: String = "",
    val username: String = "", // Наш новый никнейм
    val avatarUrl: String = "" // Фото профиля
)