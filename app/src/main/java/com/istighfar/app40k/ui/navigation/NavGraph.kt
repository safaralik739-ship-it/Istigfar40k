package com.istighfar.app40k.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.istighfar.app40k.ui.ViewModelFactory
import com.istighfar.app40k.ui.screens.achievements.AchievementsScreen
import com.istighfar.app40k.ui.screens.history.HistoryScreen
import com.istighfar.app40k.ui.screens.home.HomeScreen
import com.istighfar.app40k.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Главная")
    data object History : Screen("history", "Статистика")
    data object Achievements : Screen("achievements", "Достижения")
    data object Settings : Screen("settings", "Настройки")
}

private val bottomNavItems = listOf(Screen.Home, Screen.History, Screen.Achievements, Screen.Settings)

@Composable
fun AppNavGraph(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(iconFor(screen), contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(bottom = padding.calculateBottomPadding())
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModelFactory) }
            composable(Screen.History.route) { HistoryScreen(viewModelFactory) }
            composable(Screen.Achievements.route) { AchievementsScreen(viewModelFactory) }
            composable(Screen.Settings.route) { SettingsScreen(viewModelFactory) }
        }
    }
}

private fun iconFor(screen: Screen) = when (screen) {
    Screen.Home -> Icons.Filled.Home
    Screen.History -> Icons.Filled.ShowChart
    Screen.Achievements -> Icons.Filled.EmojiEvents
    Screen.Settings -> Icons.Filled.Settings
}
