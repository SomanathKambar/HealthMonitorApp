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

import com.vkm.healthmonitor.core.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class EnergyRepository @Inject constructor(
    private val db: AppDatabase,
    private val healthConnectManager: HealthConnectManager
) {
    val latestScore: Flow<DailyEnergyScore?> = db.energyScoreDao().getLatestScore()

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
