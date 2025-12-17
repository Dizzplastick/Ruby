package com.example.myapplication43.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication43.presentation.ui.components.TrackItem
import com.example.myapplication43.presentation.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val tracks by viewModel.userTracks.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ШАПКА ПРОФИЛЯ ---
        Spacer(modifier = Modifier.height(32.dp))

        // Аватарка (если нет URL, показываем заглушку)
        if (user?.avatarUrl?.isNotEmpty() == true) {
            AsyncImage(
                model = user?.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Имя и Email
        Text(
            text = user?.username ?: "Загрузка...",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = user?.email ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- СПИСОК ТРЕКОВ ---
        Text(
            text = "Мои треки (${tracks.size})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(tracks) { track ->
                // Используем наш готовый компонент, но пока без обработки клика
                TrackItem(track = track, onClick = { /* Можно добавить воспроизведение */ })
            }
        }
    }
}