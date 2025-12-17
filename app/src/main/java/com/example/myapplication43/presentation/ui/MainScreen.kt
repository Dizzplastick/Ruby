package com.example.myapplication43.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication43.presentation.navigation.Screen
import com.example.myapplication43.presentation.ui.components.MiniPlayer
import com.example.myapplication43.presentation.ui.screens.HomeScreen
import com.example.myapplication43.presentation.ui.screens.ProfileScreen
import com.example.myapplication43.presentation.ui.screens.UploadScreen

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    // Список экранов для нижней панели
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Upload,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            Column {
                // Вставляем мини-плеер
                MiniPlayer(
                    onClick = {
                        navController.navigate(Screen.Player.route)
                    }
                )

                // Стандартное нижнее меню
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Upload.route) { UploadScreen() }
            // ПЕРЕДАЕМ onLogout В ПРОФИЛЬ
            composable(Screen.Profile.route) {
                ProfileScreen(onLogout = onLogout)
            }
            // ДОБАВЛЯЕМ МАРШРУТ ПЛЕЕРА
            composable(Screen.Player.route) {
                PlayerScreen()
            }
        }
    }
}