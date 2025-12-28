package com.example.exam.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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