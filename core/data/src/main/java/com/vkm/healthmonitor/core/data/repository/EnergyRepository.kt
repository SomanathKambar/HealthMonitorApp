package com.vkm.healthmonitor.core.data.repository

import com.vkm.healthmonitor.core.database.AppDatabase
import com.vkm.healthmonitor.core.healthconnect.HealthConnectManager
import com.vkm.healthmonitor.core.model.DailyEnergyScore
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

import com.vkm.healthmonitor.core.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class EnergyRepository @Inject constructor(
    private val db: AppDatabase,
    private val healthConnectManager: HealthConnectManager
) {
    val latestScore: Flow<DailyEnergyScore?> = db.energyScoreDao().getLatestScore()
    val history: Flow<List<DailyEnergyScore>> = db.energyScoreDao().getHistory()

    /**
     * Estimates current body battery based on latest synced score (yesterday's performance)
     * and the time elapsed today since wake-up, plus manual adjustments.
     */
    suspend fun getDynamicEnergyScore(latestSync: DailyEnergyScore?, wakeTime: java.time.Instant?): Int {
        if (latestSync == null) return 75 // Default baseline

        val baseScore = latestSync.score.toFloat() // Baseline from yesterday
        val now = java.time.Instant.now()
        
        // If no wake time, we assume standard 7 AM
        val actualWake = wakeTime ?: java.time.Instant.now().atZone(java.time.ZoneId.systemDefault())
            .toLocalDate().atTime(7, 0).atZone(java.time.ZoneId.systemDefault()).toInstant()

        val hoursSinceWake = java.time.Duration.between(actualWake, now).toMinutes() / 60f
        
        // Drain rate: ~4% per hour of wakefulness
        val drain = (hoursSinceWake * 4.2f).coerceAtLeast(0f)
        
        // Manual adjustments for today
        val startOfToday = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val manualChange = db.energyAdjustmentDao().getTotalChangeSince(startOfToday) ?: 0
        
        // Final score starts at baseline, drains, and applies manual tweaks
        return (baseScore - drain + manualChange).toInt().coerceIn(5, 100)
    }

    suspend fun adjustEnergy(type: String, change: Int, note: String) {
        db.energyAdjustmentDao().insert(
            com.vkm.healthmonitor.core.model.EnergyAdjustment(
                type = type,
                change = change,
                note = note
            )
        )
    }

    suspend fun seedHistoryIfEmpty() = withContext(Dispatchers.IO) {
        // Seeding logic preserved but won't be called automatically if you prefer.
        val count = db.energyScoreDao().getHistory().first().size
        if (count == 0) {
            // Seeding logic...
        }
    }

    suspend fun syncDailyScore(profile: Profile? = null) = withContext(Dispatchers.IO) {
        val sleepGoal = profile?.dailySleepGoalHours ?: 8f
        val stepGoal = profile?.dailyStepGoal ?: 10000

        var sleepScore = 0
        var stepScore = 0

        if (healthConnectManager.isAvailable() && healthConnectManager.hasAllPermissions()) {
            val now = Instant.now()
            val yesterday = now.minus(1, ChronoUnit.DAYS)
            val startOfYesterday = yesterday.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfYesterday = startOfYesterday.plus(24, ChronoUnit.HOURS)

            // Steps from yesterday (Strain)
            val steps = healthConnectManager.readSteps(startOfYesterday, endOfYesterday)
            
            // Sleep from last night
            val sleepRecords = healthConnectManager.readSleepSessions(now.minus(14, ChronoUnit.HOURS), now)
            val sleepDurationSeconds = sleepRecords.sumOf { 
                java.time.Duration.between(it.startTime, it.endTime).seconds 
            }

            sleepScore = ((sleepDurationSeconds / 3600f) / sleepGoal * 100).toInt().coerceIn(0, 100)
            stepScore = ((steps.toFloat() / stepGoal) * 100).toInt().coerceIn(0, 100)
        } else {
            // Default scores if no auto data
            sleepScore = 75 
            stepScore = 50
        }
        
        val totalScore = (sleepScore * 0.7 + stepScore * 0.3).toInt()
        val todayDate = java.time.LocalDate.now().toString()
        
        val scoreEntity = DailyEnergyScore(
            date = todayDate,
            score = totalScore,
            sleepScore = sleepScore,
            sleepConsistencyScore = 80, // Placeholder
            activityBalanceScore = stepScore
        )

        db.energyScoreDao().insertScore(scoreEntity)
    }

    fun scheduleBackgroundSync(context: android.content.Context) {
        val request = androidx.work.PeriodicWorkRequestBuilder<com.vkm.healthmonitor.core.data.worker.EnergySyncWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        ).build()
        
        androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "EnergySync",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
