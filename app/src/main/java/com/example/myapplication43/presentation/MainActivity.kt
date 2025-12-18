package com.example.myapplication43.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myapplication43.presentation.ui.MainScreen
import com.example.myapplication43.presentation.ui.screens.AuthScreen
import com.example.myapplication43.ui.theme.MyApplication43Theme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        setContent {
            // MaterialTheme задает общие цвета и шрифты
            MyApplication43Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isUserLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
                    if (isUserLoggedIn) {
                        MainScreen(onLogout = {
                            isUserLoggedIn = false
                        })
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
}