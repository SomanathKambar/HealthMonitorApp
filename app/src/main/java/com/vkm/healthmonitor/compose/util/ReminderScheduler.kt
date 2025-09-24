package com.vkm.healthmonitor.compose.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.vkm.healthmonitor.compose.receiver.ReminderReceiver

object ReminderScheduler {
    fun scheduleWaterReminder(context: Context, intervalHours: Int = 2) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("msg", "Time to drink water!")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalMillis = intervalHours * 60 * 60 * 1000L
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }

    fun scheduleVitalsReminder(context: Context) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("msg", "Please update your vitals today!")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
