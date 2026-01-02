package com.vkm.healthmonitor.core.data.repository

import com.vkm.healthmonitor.core.healthconnect.HealthConnectManager
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

import com.vkm.healthmonitor.core.datastore.EnergyPrefs
import kotlinx.coroutines.flow.first

@Singleton
class CircadianRepository @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val energyPrefs: EnergyPrefs
) {

    // Returns the wake time for TODAY. Fallback to manual preference if Health Connect fails.
    suspend fun getWakeUpTime(): Instant? {
        val autoWakeTime = if (healthConnectManager.isAvailable() && healthConnectManager.hasAllPermissions()) {
            val now = Instant.now()
            val startOfDay = now.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
            
            val sessions = healthConnectManager.readSleepSessions(startOfDay.minus(4, ChronoUnit.HOURS), now)
            sessions.sortedByDescending { it.endTime }
                .firstOrNull { 
                    it.endTime.atZone(ZoneId.systemDefault()).hour >= 4 
                }?.endTime
        } else {
            null
        }

        if (autoWakeTime != null) return autoWakeTime

        // Fallback to manual entry for today
        val manualWake = energyPrefs.wakeTime.first() ?: return null
        return Instant.ofEpochMilli(manualWake)
    }

    suspend fun setManualWakeTime(timestamp: Long) {
        energyPrefs.saveWakeTime(timestamp)
    }

    suspend fun clearWakeTime() {
        energyPrefs.clear()
    }

    data class Schedule(
        val sunlightWindow: Pair<Instant, Instant>,
        val caffeineCutoff: Instant,
        val afternoonDip: Instant
    )

    fun calculateSchedule(wakeTime: Instant): Schedule {
        return Schedule(
            sunlightWindow = wakeTime to wakeTime.plus(45, ChronoUnit.MINUTES),
            caffeineCutoff = wakeTime.plus(10, ChronoUnit.HOURS),
            afternoonDip = wakeTime.plus(7, ChronoUnit.HOURS)
        )
    }
}
