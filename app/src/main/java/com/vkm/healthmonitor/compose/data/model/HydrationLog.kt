package com.vkm.healthmonitor.compose.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "hydration",
    foreignKeys = [ForeignKey(
        entity = Profile::class,
        parentColumns = ["id"],
        childColumns = ["profileId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class HydrationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profileId: Int,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val firestoreId: String? = null
)


