package com.example.exam.ui
import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.exam.ui.screen.WaterTrackerHomeScreen
import com.example.exam.ui.onboarding.IntroScreen
import com.example.exam.ui.screen.HistoryScreen
import com.example.exam.ui.screen.SettingsScreen
import com.example.exam.viewmodel.WaterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: WaterViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val userProfile by viewModel.userProfile.collectAsState()

    // Show Splash/Onboarding if profile doesn't exist
    if (userProfile == null) {
        IntroScreen (onProfileSaved = { gender, age, weight, height, goal, sleep, wake ->
            viewModel.saveProfile(gender, age, weight, height, goal, sleep, wake)
        })
    } else {
        Scaffold(
            bottomBar = {
                Column {
                    TabRow(
                        selectedTabIndex = navigationItems.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
                    ) {
                        navigationItems.forEach { screen ->
                            Tab(
                                selected = currentRoute == screen.route,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                text = { Text(screen.title) },
                                icon = { Icon(painter = painterResource(screen.icon), contentDescription = null, modifier=Modifier.size(24.dp)
                                ) }
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
                composable(Screen.Home.route) {
                    WaterTrackerHomeScreen(
                        viewModel,
                        navController
                    )
                }
                composable(Screen.History.route) {
                    HistoryScreen(
                        viewModel
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(
                        viewModel
                    )
                }

            }
        }
    }
}