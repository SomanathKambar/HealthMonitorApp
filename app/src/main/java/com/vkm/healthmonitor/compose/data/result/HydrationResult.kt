package com.vkm.healthmonitor.compose.data.result

sealed class HydrationResult {
    object Success : HydrationResult()
    data class ExceedsSafe(val safeCap: Int) : HydrationResult()
    data class Error(val reason: String) : HydrationResult()
}