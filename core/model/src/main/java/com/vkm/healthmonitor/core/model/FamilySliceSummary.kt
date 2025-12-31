package com.vkm.healthmonitor.core.model

data class HealthStatus(
    val name: String,
    val normalCount: Int,
    val warningCount: Int,
    val criticalCount: Int
)

data class FamilySliceSummary(
    val slice: SliceType,
    val count: Int,
    val items: List<ProfileSheetItem>
)

data class ProfileSheetItem(
    val profile: Profile,
    val issues: String,
    val recommendation: String
)
