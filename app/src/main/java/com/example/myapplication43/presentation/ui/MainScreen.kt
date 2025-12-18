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
import com.example.myapplication43.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val playerViewModel: PlayerViewModel = koinViewModel()



    Scaffold(
        bottomBar = {
            Column {
                // Вставляем мини-плеер
                MiniPlayer(
                    onClick = {
                        navController.navigate(Screen.Player.route)
                    },
                    onLikeClick = {
                        playerViewModel.onLikeClick()
                    }
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background, // Тёмный фон (DeepDark)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    // Список вкладок
                    val items = listOf(Screen.Home, Screen.Upload, Screen.Profile)

                    items.forEach { screen ->
                        // Проверка выделения (для профиля проверяем начало строки "profile")
                        val isSelected = if (screen is Screen.Profile) {
                            currentRoute?.startsWith("profile") == true
                        } else {
                            currentRoute == screen.route
                        }

                        NavigationBarItem(
                            icon = { Icon(screen.icon, null) },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                // ИСПРАВЛЕНИЕ: Используем Screen.Profile напрямую для вызова createRoute
                                val route = if (screen is Screen.Profile) {
                                    Screen.Profile.createRoute("me")
                                } else {
                                    screen.route
                                }
                                navController.navigate(route)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary, // RubyRed
                                selectedTextColor = MaterialTheme.colorScheme.primary, // RubyRed
                                indicatorColor = MaterialTheme.colorScheme.background, // Без овала вокруг иконки
                                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
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