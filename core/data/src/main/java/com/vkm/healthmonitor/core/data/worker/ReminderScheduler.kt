package com.vkm.healthmonitor.core.data.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleWaterReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(2, TimeUnit.HOURS)
            .setInputData(workDataOf("msg" to "Time to drink water!"))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "water_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleVitalsReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInputData(workDataOf("msg" to "Please update your vitals today!"))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "vitals_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
