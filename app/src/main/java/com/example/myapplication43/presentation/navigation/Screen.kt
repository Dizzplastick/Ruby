package com.example.myapplication43.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String,
                    val title: String,
                    val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Upload : Screen("upload", "Upload", Icons.Default.CloudUpload)

    //route шаблон с аргументом
    object Profile : Screen("profile/{userId}", "Profile", Icons.Default.AccountCircle) {
        // Вспомогательная функция для создания ссылки
        fun createRoute(userId: String) = "profile/$userId"
    }

    // Экран плеера на весь экран
    object Player : Screen("player_full", "Player", Icons.Default.Home)
}