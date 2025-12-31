package com.vkm.healthmonitor.core.common.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.vkm.healthmonitor.core.common.constant.AppConstants

object ReminderScheduler {
    fun scheduleReminder(context: Context, timeInMillis: Long, msg: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(AppConstants.REMINDER_ACTION).apply {
            setPackage(context.packageName)
            putExtra("msg", msg)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            msg.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, msg: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(AppConstants.REMINDER_ACTION).apply {
            setPackage(context.packageName)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            msg.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleWaterReminder(context: Context) {
        // Schedule a repeating water reminder every 2 hours as a simple implementation
        val interval = 2 * 60 * 60 * 1000L
        val triggerAtAt = System.currentTimeMillis() + interval
        scheduleReminder(context, triggerAtAt, "Time to drink water!")
    }

    fun scheduleVitalsReminder(context: Context) {
        // Schedule a daily vitals check reminder
        val interval = 24 * 60 * 60 * 1000L
        val triggerAtAt = System.currentTimeMillis() + interval
        scheduleReminder(context, triggerAtAt, "Don't forget to check your vitals!")
    }
}
