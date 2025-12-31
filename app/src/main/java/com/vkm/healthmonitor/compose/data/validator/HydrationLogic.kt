package com.vkm.healthmonitor.compose.data.validator

import com.vkm.healthmonitor.core.model.Profile

object HydrationLogic {
    /**
     * Compute daily goal in ml, using weight-based rule + age factor, 
     * ensuring at least a default.
     */
    fun computeDailyGoal(profile: Profile): Int {
        if (profile.weightKg <= 0f) {
            return profile.dailyWaterGoalMl
        }
        val base = (profile.weightKg * 35).toInt()  // 35 ml per kg
        val ageFactor = when (profile.age) {
            in 0..12 -> 0.8f
            in 13..18 -> 0.9f
            in 19..50 -> 1.0f
            in 51..70 -> 0.9f
            else -> 0.8f
        }
        val goal = (base * ageFactor).toInt()
        return goal.coerceAtLeast(profile.dailyWaterGoalMl)
    }

    /**
     * Max safe intake (cap) to avoid over‑hydration.
     * E.g. allow up to 150% of goal (or a fixed cap).
     */
    fun maxSafeIntake(goalMl: Int): Int {
        return (goalMl * 1.5f).toInt()
    }
}
