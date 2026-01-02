package com.vkm.healthmonitor.core.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vkm.healthmonitor.core.common.util.NotificationHelper
import com.vkm.healthmonitor.core.data.repository.CircadianRepository
import com.vkm.healthmonitor.core.data.repository.EnergyRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Instant
import java.time.temporal.ChronoUnit

@HiltWorker
class EnergySyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val energyRepository: EnergyRepository,
    private val circadianRepository: CircadianRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Sync Data
            energyRepository.syncDailyScore()
            
            // 2. Check Circadian Triggers
            checkCircadianTriggers()

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun checkCircadianTriggers() {
        NotificationHelper.createNotificationChannel(appContext)
        
        val wakeTime = circadianRepository.getWakeUpTime() ?: return
        val schedule = circadianRepository.calculateSchedule(wakeTime)
        val now = Instant.now()

        // Threshold for "Just happened" (e.g., within last 20 mins) to avoid spamming old alerts
        val recentThreshold = 20L 

        // 1. Sunlight Alert (Immediately after wake up)
        // If now is within wakeTime + 45mins, and we haven't shown it yet (tracking shown state is harder without local store, 
        // for now we just check if we are in the window close to start).
        // Actually, simple logic: if now is between wakeTime and wakeTime + 20min.
        if (isWithin(now, wakeTime, 20)) {
             NotificationHelper.showNotification(
                 appContext, 
                 101, 
                 "☀️ View Sunlight Now", 
                 "Get 10-20 mins of sunlight to anchor your energy for the day."
             )
        }

        // 2. Caffeine Cutoff
        if (isWithin(now, schedule.caffeineCutoff, 20)) {
            NotificationHelper.showNotification(
                appContext,
                102,
                "☕ Caffeine Cutoff",
                "Stop caffeine now to protect deep sleep tonight."
            )
        }

        // 3. Afternoon Dip (NSDR)
        if (isWithin(now, schedule.afternoonDip, 20)) {
             NotificationHelper.showNotification(
                 appContext,
                 103,
                 "🔋 Energy Dip Detected",
                 "You are entering your natural trough. Try 10min NSDR instead of coffee."
             )
        }
    }

    private fun isWithin(now: Instant, target: Instant, minutesBuffer: Long): Boolean {
        val diff = java.time.Duration.between(target, now).toMinutes()
        // If diff is positive (now is after target) and less than buffer
        return diff in 0..minutesBuffer
    }
}
