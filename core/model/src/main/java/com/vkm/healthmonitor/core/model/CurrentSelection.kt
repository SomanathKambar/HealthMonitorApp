package com.vkm.healthmonitor.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table to keep currently selected profile id
 */
@Entity(tableName = "current_selection")
data class CurrentSelection(
    @PrimaryKey val key: Int = 1,
    val profileId: Int = 0
)
