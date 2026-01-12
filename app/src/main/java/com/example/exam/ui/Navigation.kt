package com.example.exam.ui


import com.example.exam.R

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("home", "Home", R.drawable.home)
    object History : Screen("history", "History", R.drawable.document)
    object Settings : Screen("settings", "Settings", R.drawable.settings)
}

val navigationItems = listOf(
    Screen.Home,
    Screen.History,
    Screen.Settings
)