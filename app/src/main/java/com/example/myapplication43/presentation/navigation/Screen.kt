package com.example.myapplication43.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Главная", Icons.Default.Home)
    object Upload : Screen("upload", "Загрузка", Icons.Default.CloudUpload)

    // Меняем route на шаблон с аргументом
    object Profile : Screen("profile/{userId}", "Профиль", Icons.Default.AccountCircle) {
        // Вспомогательная функция для создания ссылки
        fun createRoute(userId: String) = "profile/$userId"
    }

    // Экран плеера (на весь экран), пока без иконки, так как он не в меню
    object Player : Screen("player_full", "Плеер", Icons.Default.Home)
}