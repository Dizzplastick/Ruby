package com.example.myapplication43.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication43.presentation.navigation.Screen
import com.example.myapplication43.presentation.ui.components.MiniPlayer
import com.example.myapplication43.presentation.ui.screens.HomeScreen
import com.example.myapplication43.presentation.ui.screens.ProfileScreen
import com.example.myapplication43.presentation.ui.screens.UploadScreen

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()



    Scaffold(
        bottomBar = {
            Column {
                // Вставляем мини-плеер
                MiniPlayer(
                    onClick = {
                        navController.navigate(Screen.Player.route)
                    }
                )

                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Главная
                    NavigationBarItem(
                        icon = { Icon(Screen.Home.icon, null) },
                        label = { Text(Screen.Home.title) },
                        selected = currentRoute == Screen.Home.route,
                        onClick = { navController.navigate(Screen.Home.route) }
                    )

                    // Загрузка
                    NavigationBarItem(
                        icon = { Icon(Screen.Upload.icon, null) },
                        label = { Text(Screen.Upload.title) },
                        selected = currentRoute == Screen.Upload.route,
                        onClick = { navController.navigate(Screen.Upload.route) }
                    )

                    // Профиль (кнопка меню всегда ведет в "me")
                    val isProfileTab = currentRoute?.startsWith("profile") == true
                    NavigationBarItem(
                        icon = { Icon(Screen.Profile.icon, null) },
                        label = { Text(Screen.Profile.title) },
                        selected = isProfileTab,
                        onClick = { navController.navigate(Screen.Profile.createRoute("me")) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAuthorClick = { userId ->
                        // Переход на профиль автора
                        navController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }
            composable(Screen.Upload.route) { UploadScreen() }
            // ПЕРЕДАЕМ onLogout В ПРОФИЛЬ
            composable(
                route = Screen.Profile.route, // "profile/{userId}"
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: "me"
                ProfileScreen(userId = userId, onLogout = onLogout)

            }
            // ДОБАВЛЯЕМ МАРШРУТ ПЛЕЕРА
            composable(Screen.Player.route) {
                PlayerScreen(
                    onAuthorClick = { userId ->
                        // Навигация на профиль человека
                        navController.navigate(Screen.Profile.createRoute(userId))
                    }
                )
            }
        }
    }
}