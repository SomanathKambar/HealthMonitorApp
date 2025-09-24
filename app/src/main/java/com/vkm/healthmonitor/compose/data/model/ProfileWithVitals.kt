package com.vkm.healthmonitor.compose.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProfileWithVitals(
    @Embedded val profile: Profile,
    @Relation(parentColumn = "id", entityColumn = "profileId")
    val vitals: List<VitalEntry>
)

