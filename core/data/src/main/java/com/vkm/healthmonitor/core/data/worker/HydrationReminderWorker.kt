package com.vkm.healthmonitor.core.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.common.validator.HydrationLogic
import com.vkm.healthmonitor.core.datastore.HydrationPrefs
import com.vkm.healthmonitor.core.common.util.HydrationUtil
import com.vkm.healthmonitor.core.common.util.NotificationUtil
import com.vkm.healthmonitor.core.common.constant.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HydrationReminderWorker(private val ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val profileId = inputData.getInt(AppConstants.WORK_DATA_KEY_PROFILE_ID, 0)
        val profile = AppDatabase.get(ctx).profileDao().getById(profileId)
            ?: return@withContext Result.success()

        val hydrationDao = AppDatabase.get(ctx).hydrationDao()

        val (dayStart, dayEnd) = HydrationUtil.getDayBounds(System.currentTimeMillis())
        val totalToday = hydrationDao.hydrationBetween(profileId, dayStart, dayEnd).firstOrNull()?.sumOf { it.amountMl } ?: 0

        val goal = HydrationLogic.computeDailyGoal(profile)
        val maxSafe = HydrationLogic.maxSafeIntake(goal)

        if (totalToday >= maxSafe) {
            return@withContext Result.success() // No more reminders needed today
        }

        val lastLog = hydrationDao.lastForProfile(profileId)
        val lastIntakeTime = lastLog?.timestamp ?: HydrationPrefs.getLastReminderTime(ctx, profileId)

        // Check if new intake occurred since last reminder
        val newIntakes = hydrationDao.hydrationBetween(profileId, lastIntakeTime, System.currentTimeMillis()).firstOrNull()

        if (newIntakes?.isEmpty() == true) {
            NotificationUtil.showHydrationReminder(ctx, profileId)
            HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())
        }

        // ✅ Compute dynamic interval
        val nextIntervalMins = HydrationUtil.computeNextReminderIntervalMinutes(
            lastIntakeTime = lastIntakeTime,
            currentTotalMl = totalToday,
            goalMl = goal,
            safeMaxMl = maxSafe
        )

        if (nextIntervalMins > 0) {
            scheduleNextReminder(ctx, profileId, nextIntervalMins, TimeUnit.MINUTES)
        }

        Result.success()
    }

    companion object {
        fun scheduleNextReminder(context: Context, profileId: Int, interval: Long, unit: TimeUnit) {
            val req = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
                .setInitialDelay(interval, unit)
                .setInputData(workDataOf(AppConstants.WORK_DATA_KEY_PROFILE_ID to profileId))
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "hydration_for_$profileId",
                ExistingWorkPolicy.REPLACE,
                req
            )
        }
    }
}
