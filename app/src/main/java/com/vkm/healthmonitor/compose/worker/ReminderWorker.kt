package com.vkm.healthmonitor.compose.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vkm.healthmonitor.R

class ReminderWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val msg = inputData.getString("msg") ?: "Health Reminder"
        val channelId = "health_reminder_channel"
        val notif = NotificationCompat.Builder(applicationContext, channelId)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle("Health Monitor")
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(applicationContext).notify((0..100000).random(), notif)
        return Result.success()
    }
}

