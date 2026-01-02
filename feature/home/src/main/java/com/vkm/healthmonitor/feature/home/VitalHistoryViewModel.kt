package com.vkm.healthmonitor.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.vkm.healthmonitor.core.data.repository.EnergyRepository
import com.vkm.healthmonitor.core.data.repository.HealthRepository
import com.vkm.healthmonitor.core.data.repository.VitalRepository
import com.vkm.healthmonitor.core.model.DailyEnergyScore
import com.vkm.healthmonitor.core.model.VitalEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class VitalHistoryUiState(
    val energyScores: List<DailyEnergyScore> = emptyList(),
    val vitals: List<VitalEntry> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class VitalHistoryViewModel @Inject constructor(
    private val energyRepository: EnergyRepository,
    private val healthRepository: HealthRepository,
    private val vitalRepository: VitalRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<VitalHistoryUiState> = combine(
        energyRepository.history,
        healthRepository.currentProfileIdFlow(),
        _isLoading
    ) { scores, profileId, loading ->
        val profileVitals = if (profileId != null) {
            vitalRepository.vitalsListFor(profileId)
        } else {
            emptyList()
        }
        
        VitalHistoryUiState(
            energyScores = scores.take(7).reversed(), // Show last 7 days chronologically
            vitals = profileVitals.sortedByDescending { it.timestamp },
            isLoading = loading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VitalHistoryUiState())

    fun seedData() {
        viewModelScope.launch {
            _isLoading.value = true
            energyRepository.seedHistoryIfEmpty()
            _isLoading.value = false
        }
    }
}
