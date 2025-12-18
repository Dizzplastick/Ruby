package com.example.myapplication43.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication43.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder

@Composable
fun MiniPlayer(
    viewModel: PlayerViewModel = koinViewModel(),
    onClick: () -> Unit ,// Лямбда для открытия полного плеера (потом реализуем)
    onLikeClick: () -> Unit, // <--- Новый колбэк
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val track = state.currentTrack

    // Если трек не выбран — скрываем мини-плеер (возвращаем пустоту)
    if (track == null) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() }, // Весь бар кликабельный
        color = MaterialTheme.colorScheme.surfaceVariant, // Немного темнее фона
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Обложка
            AsyncImage(
                model = track.coverUri,
                contentDescription = "Mini Cover",
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Название и Артист
            Column(
                modifier = Modifier.weight(1f) // Занимает всё свободное место
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. Кнопка Play/Pause
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause"
                )
            }
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    // 2. МЕНЯЕМ ЦВЕТ: Если лайк есть -> Красный, иначе -> Цвет текста
                    tint = if (track.isLiked) androidx.compose.ui.graphics.Color.Red else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}