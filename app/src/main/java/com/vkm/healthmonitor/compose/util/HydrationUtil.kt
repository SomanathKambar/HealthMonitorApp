package com.vkm.healthmonitor.compose.util

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.vkm.healthmonitor.compose.worker.HydrationReminderWorker
import com.vkm.healthmonitor.constant.AppConstants
import java.util.Calendar
import java.util.concurrent.TimeUnit

object  HydrationUtil {
    fun getDayBounds(now: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply { timeInMillis = now }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }

    fun computeNextReminderIntervalMinutes(
        lastIntakeTime: Long,
        currentTotalMl: Int,
        goalMl: Int,
        safeMaxMl: Int
    ): Long {
        if (currentTotalMl >= safeMaxMl) {
            return -1 // No more reminders needed
        }

        val now = System.currentTimeMillis()
        val minsSinceLast = ((now - lastIntakeTime) / 60000L).toInt()

        val percentage = currentTotalMl.toFloat() / goalMl.toFloat()

        val baseInterval = when {
            percentage < 0.25f -> 60
            percentage < 0.5f -> 90
            percentage < 0.75f -> 120
            percentage < 0.95f -> 150
            else -> 180
        }

        // Adjust remaining time based on how long ago last intake was
        return (baseInterval - minsSinceLast).coerceAtLeast(30).toLong()
    }



    fun scheduleNextReminder(context: Context, profileId: Int, intervalMinutes: Long) {
        val req = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            .setInitialDelay(intervalMinutes, TimeUnit.MINUTES)
            .setInputData(workDataOf(AppConstants.WORK_DATA_KEY_PROFILE_ID to profileId))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "hydration_for_$profileId",
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

}
