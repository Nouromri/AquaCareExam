package com.example.exam.data

import com.example.exam.data.dao.UserProfileDao
import com.example.exam.data.dao.WaterLogDao
import com.example.exam.data.entity.UserProfile
import com.example.exam.data.entity.WaterLog
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class WaterRepository(
    private val waterLogDao: WaterLogDao,
    private val userProfileDao: UserProfileDao
) {
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()
    val userProfile: Flow<UserProfile?> = userProfileDao.getProfile()

    fun getTodayTotal(): Flow<Int?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return waterLogDao.getTodayTotal(calendar.timeInMillis)
    }

    suspend fun addLog(amountMl: Int) {
        waterLogDao.insert(WaterLog(amountMl = amountMl))
    }

    suspend fun deleteLog(log: WaterLog) {
        waterLogDao.delete(log)
    }

    suspend fun clearLogs() {
        waterLogDao.deleteAll()
    }

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.insertProfile(profile)
    }
}
