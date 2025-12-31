package com.vkm.healthmonitor.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plans")
data class HealthPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val profileId: Int,
    val title: String,
    val description: String,
    val repeatHours: Int = 24, // repeat interval for notifications (hours)
    val active: Boolean = true,
    val source: String = "local" // "local" or "server"
)
