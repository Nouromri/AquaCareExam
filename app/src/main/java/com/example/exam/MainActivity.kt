package com.example.exam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exam.data.WaterRepository
import com.example.exam.data.bd.WaterDatabase
import com.example.exam.ui.MainScreen
import com.example.exam.ui.onboarding.IntroScreen
import com.example.exam.ui.theme.ExamTheme
import com.example.exam.viewmodel.WaterViewModel
import com.example.exam.viewmodel.WaterViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { WaterDatabase.getDatabase(this) }
    private val repository by lazy {
        WaterRepository(
            database.waterLogDao(),
            database.userProfileDao()
        )
    }
    private val viewModel: WaterViewModel by viewModels {
        WaterViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}