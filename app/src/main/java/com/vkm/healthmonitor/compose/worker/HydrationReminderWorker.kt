package com.vkm.healthmonitor.compose.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.compose.data.validator.HydrationLogic
import com.vkm.healthmonitor.core.datastore.HydrationPrefs
import com.vkm.healthmonitor.compose.util.HydrationUtil
import com.vkm.healthmonitor.compose.util.NotificationUtil
import com.vkm.healthmonitor.constant.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext


//class HydrationReminderWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        val profileId = inputData.getInt(AppConstants.WORK_DATA_KEY_PROFILE_ID, 0)
//        NotificationUtil.showHydrationReminder(ctx, profileId)
//        Result.success()
//    }
//}


//class HydrationReminderWorker @Inject constructor(@ApplicationContext private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        val profileId = inputData.getInt(AppConstants.WORK_DATA_KEY_PROFILE_ID, -1)
//
//        val lastReminderTime = HydrationPrefs.getLastReminderTime(ctx, profileId)
//
//        val db = AppDatabase.get(ctx)
//        val intakeAfterLastReminder = db.hydrationDao().getIntakeAfter(profileId, lastReminderTime)
//
//        if (intakeAfterLastReminder == null) {
//            NotificationUtil.showHydrationReminder(ctx, profileId)
//            HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())
//        }
//
//        // Always reschedule next check
//        scheduleNextReminder(profileId)
//
//        return@withContext Result.success()
//    }
//
//    private fun scheduleNextReminder(profileId: Int, intervalHours: Long = 2L) {
//        val req = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
//            .setInitialDelay(intervalHours, TimeUnit.HOURS)
//            .setInputData(workDataOf(AppConstants.WORK_DATA_KEY_PROFILE_ID to profileId))
//            .build()
//
//        WorkManager.getInstance(ctx).enqueueUniqueWork("hydration_for_$profileId",
//            ExistingWorkPolicy.REPLACE, req)
//    }
//}


class HydrationReminderWorker(private val ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {
//    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
//        val profileId = inputData.getInt(AppConstants.WORK_DATA_KEY_PROFILE_ID, 0)
//        val profile = AppDatabase.get(ctx).profileDao().getById(profileId)
//            ?: return@withContext Result.success()
//
//        val (dayStart, dayEnd) = getDayBounds(System.currentTimeMillis())
//        val totalToday = AppDatabase.get(ctx)
//            .hydrationDao()
//            .hydrationBetween(profileId, dayStart, dayEnd).firstOrNull()
//            ?.sumOf { it.amountMl } ?: 0
//
//        val goal = HydrationLogic.computeDailyGoal(profile)
//        val safeMax = HydrationLogic.maxSafeIntake(goal)
//
//        // If already above safe max, skip reminder
//        if (totalToday >= safeMax) {
//            return@withContext Result.success()
//        }
//
//        // Check intake since last reminder
//        val lastRem = HydrationPrefs.getLastReminderTime(ctx, profileId)
//        val newIntakes = AppDatabase.get(ctx).hydrationDao()
//            .hydrationBetween(profileId, lastRem, System.currentTimeMillis()).firstOrNull()
//
//        if (newIntakes?.isEmpty() == true) {
//            NotificationUtil.showHydrationReminder(ctx, profileId)
//            HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())
//        }
//
//        scheduleNextReminder(profileId)
//        return@withContext Result.success()
//    }
//
//    private fun scheduleNextReminder(profileId: Int, intervalHours: Long = 2L) {
//        val req = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
//            .setInitialDelay(intervalHours, TimeUnit.HOURS)
//            .setInputData(workDataOf(AppConstants.WORK_DATA_KEY_PROFILE_ID to profileId))
//            .build()
//        WorkManager.getInstance(ctx).enqueueUniqueWork("hydration_for_$profileId", ExistingWorkPolicy.REPLACE, req)
//    }
//
//}
//


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
        val minsSinceLast = ((System.currentTimeMillis() - lastIntakeTime) / 60000L)

        // Check if new intake occurred since last reminder
        val newIntakes = hydrationDao.hydrationBetween(profileId, lastIntakeTime, System.currentTimeMillis()).firstOrNull()

        if (newIntakes?.isEmpty() == true) {
            NotificationUtil.showHydrationReminder(ctx, profileId)
            HydrationPrefs.setLastReminderTime(ctx, profileId, System.currentTimeMillis())
        }

        // ✅ Compute dynamic interval using minsSinceLast
        val nextIntervalMins = HydrationUtil.computeNextReminderIntervalMinutes(
            lastIntakeTime = minsSinceLast,
            currentTotalMl = totalToday,
            goalMl = goal,
            safeMaxMl = maxSafe
        )

        if (nextIntervalMins > 0) {
            HydrationUtil.scheduleNextReminder(ctx, profileId, nextIntervalMins)
        }

        Result.success()
    }


}
