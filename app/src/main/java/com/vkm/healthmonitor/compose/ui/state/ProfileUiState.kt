package com.vkm.healthmonitor.compose.ui.state

data class ProfileUiState(
    val id: Int = 0,
    val name: String = "",
    val age: String = "",
    val gender: String = "Male",
    val relation: String = "Self",
    val height: String = "",
    val weight: String = "",
    val waterGoal: String = "2000"
) {
    companion object {
        fun computeBmi(heightCm: Float, weightKg: Float): Float {
            val h = heightCm / 100f
            return if (h > 0f) (weightKg / (h * h)) else 0f
        }
    }
}

fun ProfileUiState.bmi (): Float {
    return ProfileUiState.computeBmi(height.toFloatOrNull()?:0.0f, weight.toFloatOrNull()?:0f)
}