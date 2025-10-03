package com.vkm.healthmonitor.compose.data.model

data class FamilyHealthSummary(
    val slice: SliceType,
    val count: Int,
    val profiles: List<ProfileIssue>
)