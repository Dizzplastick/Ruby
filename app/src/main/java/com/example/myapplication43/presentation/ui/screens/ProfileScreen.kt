package com.example.myapplication43.presentation.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication43.presentation.ui.components.TrackItem
import com.example.myapplication43.presentation.viewmodel.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    userId: String,
    onLogout: () -> Unit, // <--- Добавили колбэк для выхода
    viewModel: ProfileViewModel = koinViewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadProfileData(userId)
    }

    val user by viewModel.currentUser.collectAsState()
    val tracks by viewModel.userTracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // ВАЖНО: Определяем, хозяин ли я
    val isMyProfile = remember(userId) { viewModel.isMyProfile(userId) }

    // Состояния для режима редактирования
    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf("") }
    var editedAvatarUri by remember { mutableStateOf<Uri?>(null) }

    // Пикер для выбора новой аватарки
    val imageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            editedAvatarUri = uri



        }



    // При входе в режим редактирования заполняем поле текущим именем
    LaunchedEffect(isEditing) {
        if (isEditing) {
            editedUsername = user?.username ?: ""
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка ВЫХОД (слева или справа, как удобнее)
                if (isMyProfile) {
                    IconButton(onClick = { viewModel.logout(); onLogout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp)) // Пустышка для отступа, если кнопки нет
                }



                // КНОПКИ РЕДАКТИРОВАНИЯ: Показываем ТОЛЬКО если isMyProfile
                if (isMyProfile) {
                    if (isEditing) {
                        Row {
                            // Кнопки Сохранить/Отмена...
                            IconButton(onClick = { isEditing = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel")
                            }
                            IconButton(onClick = {
                                viewModel.updateProfile(editedUsername, editedAvatarUri)
                                isEditing = false
                            }) {
                                Icon(Icons.Default.Save, contentDescription = "Save")
                            }
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- АВАТАРКА ---
            Box(contentAlignment = Alignment.Center) {
                val avatarModifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .let {
                        if (isEditing) it.clickable { imageLauncher.launch("image/*") } else it
                    }

                // Логика отображения: Если выбрали новую (URI) -> показываем её, иначе -> URL из базы
                if (editedAvatarUri != null) {
                    AsyncImage(
                        model = editedAvatarUri,
                        contentDescription = "New Avatar",
                        modifier = avatarModifier,
                        contentScale = ContentScale.Crop
                    )
                } else if (user?.avatarUrl?.isNotEmpty() == true) {
                    AsyncImage(
                        model = user?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = avatarModifier,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = avatarModifier.background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Иконка "изменить фото" поверх
                if (isEditing) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ИМЯ ПОЛЬЗОВАТЕЛЯ ---
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (isEditing) {
                    OutlinedTextField(
                        value = editedUsername,
                        onValueChange = { editedUsername = it },
                        label = { Text("Никнейм") },
                        singleLine = true
                    )
                } else {
                    Text(
                        text = user?.username ?: "Без имени",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = user?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- СПИСОК ТРЕКОВ ---
            if (!isEditing) {
                Text(
                    text = "Мои треки (${tracks.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(tracks) { track ->
                        TrackItem(
                            track = track,
                            onClick = { /* Тут логика воспроизведения */ },
                            onAuthorClick = {
                                // В профиле мы уже находимся на странице автора,
                                // поэтому клик по имени можно игнорировать (пустая лямбда)
                            }
                        )
                    }
                }
            }
        }
    }
}