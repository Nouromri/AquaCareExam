package com.example.exam.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.exam.data.entity.WaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface  WaterLogDao {
    @Insert
    suspend fun insert(waterLog: WaterLog)

    @Delete
    suspend fun delete(waterLog: WaterLog)

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE timestamp >= :startOfDay")
    fun getTodayTotal(startOfDay: Long): Flow<Int?>

    @Query("DELETE FROM water_logs")
    suspend fun deleteAll()
}
