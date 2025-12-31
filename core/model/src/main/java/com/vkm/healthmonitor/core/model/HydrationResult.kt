package com.vkm.healthmonitor.core.model

sealed class HydrationResult {
    object Success : HydrationResult()
    data class ExceedsSafe(val safeCap: Int) : HydrationResult()
    data class Error(val reason: String) : HydrationResult()
}