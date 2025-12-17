package com.example.myapplication43.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.myapplication43.presentation.ui.MainScreen
import com.example.myapplication43.presentation.ui.screens.AuthScreen
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        setContent {
            // MaterialTheme задает общие цвета и шрифты
            MaterialTheme {
                var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
                if (isUserLoggedIn) {
                    MainScreen()
                } else {
                    // --- ЭКРАН ВХОДА ---
                    AuthScreen(onLoginSuccess = {
                        isUserLoggedIn = true
                    })
                }
            }
        }
    }
}