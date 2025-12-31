package com.vkm.healthmonitor.core.model

data class FamilyHealthSummary(
    val slice: SliceType,
    val count: Int,
    val profiles: List<ProfileIssue>
)