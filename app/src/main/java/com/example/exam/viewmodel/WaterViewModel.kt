package com.example.exam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.exam.data.WaterRepository
import com.example.exam.data.entity.UserProfile
import com.example.exam.data.entity.WaterLog
import com.example.exam.network.WeatherClient
import com.example.exam.network.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class HistoryEntry(val time: String, val amount: Int)
class WaterViewModel(private val repository: WaterRepository) : ViewModel() {
    private val _history = MutableStateFlow(listOf(
        HistoryEntry("10:30", 200),
    ))
    val history: StateFlow<List<HistoryEntry>> = _history

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = WeatherClient.api.getWeather(lat, lon)
                _weather.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    val todayTotal: StateFlow<Int> = repository.getTodayTotal()
        .map { it ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allLogs: StateFlow<List<WaterLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    val cupSize: Int get() = userProfile.value?.cupSize ?: 200
    val dailyGoal: Int
        get() = userProfile.value?.dailyGoalInMl ?: 2000

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addLog(amountMl)
        }
    }


    fun deleteLog(log: WaterLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearLogs()
        }
    }

    fun updateProfile(profile: UserProfile) {
        val cappedProfile = profile.copy(dailyGoalInMl = profile.dailyGoalInMl.coerceAtMost(10000))
        viewModelScope.launch {
            repository.saveProfile(cappedProfile)
        }
    }

    fun saveProfile(
        gender: String,
        age: Int,
        weight: Int,
        height: Int,
        dailyGoal: Int,
        sleepTime: String,
        wakeTime: String,
        cupSize: Int = 250
    ) {
        viewModelScope.launch {
            repository.saveProfile(
                UserProfile(
                    gender = gender,
                    age = age,
                    weight = weight,
                    height = height,
                    dailyGoalInMl = dailyGoal.coerceAtMost(10000),
                    sleepTime = sleepTime,
                    wakeTime = wakeTime,
                    cupSize = cupSize,
                    notificationsEnabled = true,
                    notificationIntervalMinutes = 10
                )
            )
        }
    }
}

class WaterViewModelFactory(private val repository: WaterRepository) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}