package com.vkm.healthmonitor.compose.data.model

data class ProfileIssue(
    val profile: Profile,
    val slice: SliceType,  // NORMAL / WARNING / CRITICAL
    val issues: String,    // e.g. "Pulse high, BP low"
    val recommendation: String
)



