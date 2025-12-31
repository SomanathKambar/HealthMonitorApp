package com.vkm.healthmonitor.core.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vitals",
    foreignKeys = [ForeignKey(
        entity = Profile::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class VitalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val profileId: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = "", // set by repo using DateUtils.currentDateString()
    val pulse: Int = 0,
    val bpSys: Int = 0,
    val bpDia: Int = 0,
    val temperature: Float = 0f,
    val spo2: Int = 0
)

