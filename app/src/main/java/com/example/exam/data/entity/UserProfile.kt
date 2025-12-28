package com.example.exam.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val gender: String,
    val age: Int,
    val weight: Int,
    val height: Int,
    val sleepTime: String,
    val wakeTime: String,
    val dailyGoalInMl: Int,
    val cupSize: Int,
    val notificationsEnabled: Boolean = true,
    val notificationIntervalMinutes: Int = 10
)
