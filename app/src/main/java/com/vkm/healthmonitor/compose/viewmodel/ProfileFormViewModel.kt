package com.vkm.healthmonitor.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.core.model.Profile
import com.vkm.healthmonitor.core.data.repository.ProfileRepository
import com.vkm.healthmonitor.compose.ui.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ProfileFormViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

//    private val _uiState = MutableStateFlow(ProfileUiState())
//    val uiState: StateFlow<ProfileUiState> = _uiState
//
    fun updateField(update: (ProfileUiState) -> ProfileUiState) {
        _uiState.value = update(_uiState.value)
    }
//
//    fun loadProfile(id: Int) {
//        viewModelScope.launch {
//            val p = repo.getById(id)
//            if (p != null) {
//                _uiState.value = ProfileUiState(
//                    id = p.id,
//                    name = p.name,
//                    age = p.age.toString(),
//                    gender = p.gender,
//                    relation = p.relationTo ?: "Self",
//                    height = p.heightCm.toString(),
//                    weight = p.weightKg.toString(),
//                    waterGoal = p.dailyWaterGoalMl.toString()
//                )
//            }
//        }
//    }
//
//    fun saveProfile() {
//        val s = _uiState.value
//        val h = s.height.toFloatOrNull() ?: 0f
//        val w = s.weight.toFloatOrNull() ?: 0f
//        val bmi = Profile.computeBmi(h, w)
//
//        val profile = Profile(
//            id = if (s.id == 0) 0 else s.id, // ✅ update if editing
//            name = s.name.trim(),
//            age = s.age.toIntOrNull() ?: 0,
//            gender = s.gender,
//            relationTo = s.relation,
//            heightCm = h,
//            weightKg = w,
//            dailyWaterGoalMl = s.waterGoal.toIntOrNull() ?: 2000,
//            bmi = bmi
//        )
//        viewModelScope.launch { repo.insertOrUpdate(profile) }
//    }
//
//
//    fun reset() {
//        _uiState.value = ProfileUiState()
//    }

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val _selectedProfile = MutableStateFlow<Int?>(null)
    val selectedProfile: StateFlow<Int?> = _selectedProfile

    fun reset() { _uiState.value = ProfileUiState() }

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
                    waterGoal = p.dailyWaterGoalMl.toString()
                )
            }
        }
    }

    // individual update helpers (avoid shadowing)
    fun onNameChange(v: String) { _uiState.value = _uiState.value.copy(name = v) }
    fun onAgeChange(v: String) { _uiState.value = _uiState.value.copy(age = v) }
    fun onGenderChange(v: String) { _uiState.value = _uiState.value.copy(gender = v) }
    fun onRelationChange(v: String) { _uiState.value = _uiState.value.copy(relation = v) }
    fun onHeightChange(v: String) { _uiState.value = _uiState.value.copy(height = v) }
    fun onWeightChange(v: String) { _uiState.value = _uiState.value.copy(weight = v) }
    fun onWaterGoalChange(v: String) { _uiState.value = _uiState.value.copy(waterGoal = v) }

    fun saveProfile() {
        viewModelScope.launch {
            val s = _uiState.value
            val h = s.height.toFloatOrNull() ?: 0f
            val w = s.weight.toFloatOrNull() ?: 0f
            val bmi = Profile.computeBmi(h, w)
            val profile = Profile(
                id = s.id,
                name = s.name.trim(),
                age = s.age.toIntOrNull() ?: 0,
                gender = s.gender,
                relationTo = s.relation,
                heightCm = h,
                weightKg = w,
                dailyWaterGoalMl = s.waterGoal.toIntOrNull() ?: 2000,
                bmi = bmi
            )
            repo.insertOrUpdate(profile)
        }
    }

    fun selectProfile(id: Int?) {
        _selectedProfile.value = id
    }
}
