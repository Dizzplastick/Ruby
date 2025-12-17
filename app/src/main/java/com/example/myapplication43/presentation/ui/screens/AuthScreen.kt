package com.example.myapplication43.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.myapplication43.presentation.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = koinViewModel() // Инжектим ViewModel через Koin
) {
    val context = LocalContext.current

    // Состояния полей ввода
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") } // Новое поле для никнейма

    // Состояния режима экрана
    var isLoginMode by remember { mutableStateOf(true) } // true = Вход, false = Регистрация
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Вход в Ruby" else "Регистрация",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- ПОЛЕ НИКНЕЙМА (Только при регистрации) ---
        if (!isLoginMode) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Никнейм") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- ПОЛЕ EMAIL ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- ПОЛЕ ПАРОЛЯ ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- КНОПКА ДЕЙСТВИЯ ---
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true

                        if (isLoginMode) {
                            // Логика ВХОДА
                            viewModel.loginUser(
                                email = email,
                                pass = password,
                                onSuccess = {
                                    isLoading = false
                                    onLoginSuccess()
                                },
                                onError = { errorMsg ->
                                    isLoading = false
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            // Логика РЕГИСТРАЦИИ
                            if (username.isNotBlank()) {
                                viewModel.registerUser(
                                    email = email,
                                    pass = password,
                                    username = username,
                                    onSuccess = {
                                        isLoading = false
                                        onLoginSuccess()
                                    },
                                    onError = { errorMsg ->
                                        isLoading = false
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            } else {
                                isLoading = false
                                Toast.makeText(context, "Пожалуйста, укажите никнейм", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ПЕРЕКЛЮЧАТЕЛЬ РЕЖИМА ---
        TextButton(onClick = { isLoginMode = !isLoginMode }) {
            Text(if (isLoginMode) "Нет аккаунта? Создать" else "Уже есть аккаунт? Войти")
        }
    }
}