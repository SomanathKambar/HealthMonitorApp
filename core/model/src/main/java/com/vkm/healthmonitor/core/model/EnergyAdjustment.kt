package com.vkm.healthmonitor.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "energy_adjustments")
data class EnergyAdjustment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // REST, FOOD, EXERCISE, STRESS
    val change: Int,  // e.g., +10, -15
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)
