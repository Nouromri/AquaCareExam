package com.example.exam.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.exam.R
import java.util.concurrent.TimeUnit

class WaterReminderWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "water_reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Water Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Time to Hydrate!")
            .setContentText("Stay healthy by drinking a glass of water now.")
            .setSmallIcon(R.drawable.logo1)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        fun scheduleReminder(context: Context, intervalMinutes: Int) {
            val workRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                intervalMinutes.toLong(), TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WaterReminderWork",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("WaterReminderWork")
        }
    }
}
