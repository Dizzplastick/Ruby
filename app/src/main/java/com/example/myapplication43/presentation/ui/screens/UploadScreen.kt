package com.example.myapplication43.presentation.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication43.presentation.viewmodel.UploadViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UploadScreen(
    viewModel: UploadViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val uploadSuccess by viewModel.uploadSuccess.collectAsState()

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }

    // Пикер для Картинки
    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        coverUri = uri
    }

    // Пикер для Аудио
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        audioUri = uri
    }

    // Обработка результата загрузки
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess == true) {
            Toast.makeText(context, "Трек успешно загружен!", Toast.LENGTH_LONG).show()
            // Очистка полей
            title = ""
            artist = ""
            coverUri = null
            audioUri = null
            viewModel.resetState()
        } else if (uploadSuccess == false) {
            Toast.makeText(context, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Загрузка нового трека", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Выбор обложки
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .clickable { imageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (coverUri != null) {
                    AsyncImage(model = coverUri, contentDescription = null, modifier = Modifier.fillMaxSize())
                } else {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(48.dp))
                }
            }
            Text("Нажми, чтобы выбрать обложку", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Поля ввода
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название трека") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = artist,
                onValueChange = { artist = it },
                label = { Text("Исполнитель") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Выбор аудиофайла
            Button(
                onClick = { audioLauncher.launch("audio/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (audioUri != null) Color.Green else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.AudioFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (audioUri != null) "Файл выбран" else "Выбрать MP3 файл")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Кнопка отправки
            Button(
                onClick = { viewModel.uploadTrack(title, artist, coverUri, audioUri) },
                enabled = title.isNotEmpty() && artist.isNotEmpty() && coverUri != null && audioUri != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ЗАГРУЗИТЬ ТРЕК")
            }
        }
    }
}