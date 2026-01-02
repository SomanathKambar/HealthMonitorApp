package com.vkm.healthmonitor.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.data.repository.ProfileRepository
import com.vkm.healthmonitor.feature.profile.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFormViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _selectedProfile = MutableStateFlow<Int?>(null)
    val selectedProfile: StateFlow<Int?> = _selectedProfile

    fun reset() { _uiState.value = ProfileUiState() }

    fun updateField(update: (ProfileUiState) -> ProfileUiState) {
        _uiState.value = update(_uiState.value)
    }

    fun loadProfile(id: Int) {
        viewModelScope.launch {
            val p = repo.getById(id)
            if (p != null) {
                _uiState.value = ProfileUiState(
                    id = p.id,
                    name = p.name,
                    age = p.age.toString(),
                    gender = p.gender,
                    relation = p.relationTo ?: "Self",
                    height = p.heightCm.toString(),
                    weight = p.weightKg.toString(),
                    waterGoal = p.dailyWaterGoalMl.toString(),
                    dailyStepGoal = p.dailyStepGoal.toString(),
                    dailySleepGoal = p.dailySleepGoalHours.toString(),
                    caffeineSensitivity = p.caffeineSensitivity
                )
            }
        }
    }

    fun onNameChange(v: String) { _uiState.value = _uiState.value.copy(name = v) }
    fun onAgeChange(v: String) { _uiState.value = _uiState.value.copy(age = v) }
    fun onHeightChange(v: String) { _uiState.value = _uiState.value.copy(height = v) }
    fun onWeightChange(v: String) { _uiState.value = _uiState.value.copy(weight = v) }
    fun onWaterGoalChange(v: String) { _uiState.value = _uiState.value.copy(waterGoal = v) }
    fun onStepGoalChange(v: String) { _uiState.value = _uiState.value.copy(dailyStepGoal = v) }
    fun onSleepGoalChange(v: String) { _uiState.value = _uiState.value.copy(dailySleepGoal = v) }
    fun onCaffeineChange(v: String) { _uiState.value = _uiState.value.copy(caffeineSensitivity = v) }

    fun validate(): Boolean {
        val s = _uiState.value
        val nameErr = if (s.name.isBlank()) "Name is required" else null
        val ageErr = if (s.age.toIntOrNull() == null || s.age.toInt() <= 0) "Invalid age" else null
        val heightErr = if (s.height.toFloatOrNull() == null || s.height.toFloat() <= 0) "Invalid height" else null
        val weightErr = if (s.weight.toFloatOrNull() == null || s.weight.toFloat() <= 0) "Invalid weight" else null

        _uiState.value = s.copy(
            nameError = nameErr,
            ageError = ageErr,
            heightError = heightErr,
            weightError = weightErr
        )

        return nameErr == null && ageErr == null && heightErr == null && weightErr == null
    }

    fun saveProfile() {
        viewModelScope.launch {
            val s = _uiState.value
            val h = s.height.toFloatOrNull() ?: 0f
            val w = s.weight.toFloatOrNull() ?: 0f
            val bmi = Profile.computeBmi(h, w)
            
            // Check if we already have a profile to update
            val profiles = repo.allProfilesFlow().first()
            val existingId = profiles.firstOrNull()?.id ?: s.id

            val profile = Profile(
                id = existingId,
                name = s.name.trim(),
                age = s.age.toIntOrNull() ?: 0,
                gender = s.gender,
                relationTo = s.relation,
                heightCm = h,
                weightKg = w,
                dailyWaterGoalMl = s.waterGoal.toIntOrNull() ?: 2000,
                bmi = bmi,
                dailyStepGoal = s.dailyStepGoal.toIntOrNull() ?: 10000,
                dailySleepGoalHours = s.dailySleepGoal.toFloatOrNull() ?: 8f,
                caffeineSensitivity = s.caffeineSensitivity
            )
            val newId = repo.insertOrUpdate(profile)
            repo.setCurrentProfile(newId.toInt())
        }
    }

    fun selectProfile(id: Int?) {
        _selectedProfile.value = id
        id?.let {
            viewModelScope.launch { repo.setCurrentProfile(it) }
        }
    }

    fun loadOwnerProfile() {
        viewModelScope.launch {
            val profiles = repo.allProfilesFlow().first()
            val owner = profiles.firstOrNull()
            if (owner != null) {
                _uiState.value = ProfileUiState(
                    id = owner.id,
                    name = owner.name,
                    age = owner.age.toString(),
                    gender = owner.gender,
                    relation = owner.relationTo ?: "Self",
                    height = owner.heightCm.toString(),
                    weight = owner.weightKg.toString(),
                    waterGoal = owner.dailyWaterGoalMl.toString(),
                    dailyStepGoal = owner.dailyStepGoal.toString(),
                    dailySleepGoal = owner.dailySleepGoalHours.toString(),
                    caffeineSensitivity = owner.caffeineSensitivity
                )
            }
        }
    }
}