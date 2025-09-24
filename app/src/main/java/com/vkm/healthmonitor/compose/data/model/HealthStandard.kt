package com.vkm.healthmonitor.compose.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "standards")
data class HealthStandard(
    @PrimaryKey val key: String,
    val value: String
)

