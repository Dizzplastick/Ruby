package com.example.myapplication43.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.* // Используем Material Design 3
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication43.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun PlayerScreen(
    // Koin сам найдет и подставит сюда нашу PlayerViewModel
    viewModel: PlayerViewModel = koinViewModel(),
    onAuthorClick: (String) -> Unit
) {
    // Эта строчка делает магию: как только во ViewModel изменятся данные,
    // переменная state обновится, и экран перерисуется САМ.
    val state by viewModel.uiState.collectAsState()
    val track = state.currentTrack

    // Column - это столбик. Элементы идут сверху вниз.
    Column(
        modifier = Modifier.fillMaxSize(), // Занять весь экран
        horizontalAlignment = Alignment.CenterHorizontally, // Всё по центру
        verticalArrangement = Arrangement.Center
    ) {

        // 1. Картинка альбома (используем библиотеку Coil)
        AsyncImage(
            model = state.currentTrack?.coverUri, // Ссылка на картинку
            contentDescription = "Cover",
            modifier = Modifier.size(310.dp).clip(RoundedCornerShape(9.dp))// Размер 300x300
        )

        Spacer(modifier = Modifier.height(16.dp)) // Отступ

        // 2. Текст с названием
        // 2. Текст
        Text(text = state.currentTrack?.title ?: "Not Playing", style = MaterialTheme.typography.headlineMedium)
        Text(text = state.currentTrack?.artist ?: "", style = MaterialTheme.typography.bodyLarge)

        // --- 4. НОВОЕ: ИМЯ ЗАГРУЗИВШЕГО (АВТОР) ---
        // Проверяем, есть ли имя автора, чтобы не показывать пустую строку
        if (track != null && track.username.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Загрузил: @${track.username}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary, // Цвет ссылки
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        // При клике вызываем переход на профиль
                        onAuthorClick(track.userId)
                    }
                    .padding(4.dp) // Небольшой отступ для удобства нажатия
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. ПРОГРЕСС БАР (НОВОЕ) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Slider(
                value = state.currentPosition.toFloat(),
                onValueChange = { newPos -> viewModel.onSeek(newPos) },
                valueRange = 0f..state.totalDuration.toFloat().coerceAtLeast(1f), // Защита от 0
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                )
            )

            // Время: Текущее --- Всего
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(state.currentPosition),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = formatTime(state.totalDuration),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Кнопки управления (Row)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp)
        ) {




            // Кнопка Назад
            IconButton(onClick = { viewModel.skipToPrevious() }, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", modifier = Modifier.fillMaxSize())
            }

            // Кнопка Play/Pause (Большая)
            FilledIconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Кнопка Вперед
            IconButton(onClick = { viewModel.skipToNext() }, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.fillMaxSize())
            }
        }
        // КНОПКА ЛАЙК (БОЛЬШАЯ)
        if (track != null) {
            IconButton(onClick = { viewModel.onLikeClick() }) {
                Icon(
                    // Та же логика: Favorite (полное) или FavoriteBorder (пустое)
                    imageVector = if (track.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    // Цвет: Красный или обычный
                    tint = if (track.isLiked) com.example.myapplication43.ui.theme.RubyRed else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    if (ms <= 0) return "00:00"
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}