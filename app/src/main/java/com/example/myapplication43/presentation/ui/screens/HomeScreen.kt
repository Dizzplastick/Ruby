package com.example.myapplication43.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication43.presentation.ui.components.TrackItem
import com.example.myapplication43.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onAuthorClick: (String) -> Unit
) {
    val tracks by viewModel.tracks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        // 1. Поисковая строка
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onSearch = { /* Пока не обрабатываем Enter */ },
            active = false, // Пока делаем просто как поле ввода, не расширяемое
            onActiveChange = {},
            placeholder = { Text("Поиск треков...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Тут могут быть подсказки поиска
        }

        // 2. Список треков
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp) // Отступ снизу, чтобы MiniPlayer не перекрывал последний трек
        ) {
            // Заголовок
            item {
                Text(
                    text = "Недавно добавленные",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Сами треки
            items(tracks) { track ->
                TrackItem(
                    track = track,
                    onClick = { viewModel.onTrackClick(track) },
                    onAuthorClick = { userId -> onAuthorClick(userId) } // <---
                )
            }
        }
    }
}