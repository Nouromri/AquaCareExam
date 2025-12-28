package com.example.exam.data.bd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.exam.data.dao.UserProfileDao
import com.example.exam.data.dao.WaterLogDao
import com.example.exam.data.entity.UserProfile
import com.example.exam.data.entity.WaterLog

@Database(entities = [WaterLog::class, UserProfile::class], version = 5, exportSchema = false)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun waterLogDao(): WaterLogDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var Instance: WaterDatabase? = null

        fun getDatabase(context: Context): WaterDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WaterDatabase::class.java, "water_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
