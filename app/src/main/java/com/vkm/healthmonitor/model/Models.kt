package com.vkm.healthmonitor.model
data class HealthEntry(
    val heartRate: Int = 0,
    val bpSys: Int = 0,
    val bpDia: Int = 0,
    val temperature: Double = 0.0,
    val weight: Double = 0.0,
    val spo2: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
data class HydrationEntry(
    val amountMl: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
