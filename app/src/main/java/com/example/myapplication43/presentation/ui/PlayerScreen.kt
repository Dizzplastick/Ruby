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
            modifier = Modifier.size(300.dp).clip(RoundedCornerShape(16.dp))// Размер 300x300
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
                text = "Uploaded by @${track.username}",
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

        Spacer(modifier = Modifier.height(48.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Кнопки управления (Row)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {




            // Кнопка Назад
            IconButton(onClick = { viewModel.skipToPrevious() }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Prev", modifier = Modifier.fillMaxSize())
            }

            // Кнопка Play/Pause (Большая)
            FilledIconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Кнопка Вперед
            IconButton(onClick = { viewModel.skipToNext() }, modifier = Modifier.size(48.dp)) {
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
                    tint = if (track.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}