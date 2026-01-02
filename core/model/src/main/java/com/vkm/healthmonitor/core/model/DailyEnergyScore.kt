package com.vkm.healthmonitor.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_energy_score")
data class DailyEnergyScore(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val score: Int, // 0-100
    val sleepScore: Int,
    val sleepConsistencyScore: Int,
    val activityBalanceScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)
