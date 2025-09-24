package com.vkm.healthmonitor.compose.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val age: Int = 0,
    val gender: String = "Male",
    val relationTo: String? = "Self",
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val dailyWaterGoalMl: Int = 2000,
    val bmi: Float = 0f
) {
    companion object {
        fun computeBmi(heightCm: Float, weightKg: Float): Float {
            return if (heightCm > 0f && weightKg > 0f) {
                val m = heightCm / 100f
                (weightKg / (m * m)).toFloat()
            } else 0f
        }
    }
}


