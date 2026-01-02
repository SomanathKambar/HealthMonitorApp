package com.vkm.healthmonitor.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.core.data.repository.EnergyRepository
import com.vkm.healthmonitor.core.healthconnect.HealthConnectManager
import com.vkm.healthmonitor.core.model.DailyEnergyScore
import com.vkm.healthmonitor.core.data.repository.CircadianRepository
import com.vkm.healthmonitor.core.data.repository.HealthRepository
import com.vkm.healthmonitor.core.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import kotlinx.coroutines.delay
import javax.inject.Inject

data class HomeUiState(
    val energyScore: DailyEnergyScore? = null,
    val activeProfile: Profile? = null,
    val hasPermissions: Boolean = false,
    val isHealthConnectAvailable: Boolean = true,
    val wakeTime: Instant? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val energyRepository: EnergyRepository,
    private val healthConnectManager: HealthConnectManager,
    private val circadianRepository: CircadianRepository,
    private val healthRepository: HealthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _hasPermissions = MutableStateFlow(false)
    private val _isAvailable = MutableStateFlow(true)
    private val _wakeTime = MutableStateFlow<Instant?>(null)

    // Reactive UI State derived from core flows - explicitly using combine overloads
    val uiState: StateFlow<HomeUiState> = combine(
        energyRepository.latestScore,
        healthRepository.allProfilesFlow(),
        healthRepository.currentProfileIdFlow(),
        _hasPermissions,
        _isAvailable,
        _wakeTime,
        _isLoading
    ) { args: Array<Any?> ->
        val score = args[0] as DailyEnergyScore?
        val allProfiles = args[1] as List<Profile>
        val currentId = args[2] as Int?
        val perm = args[3] as Boolean
        val avail = args[4] as Boolean
        val wake = args[5] as Instant?
        val loading = args[6] as Boolean

        val active = allProfiles.find { it.id == currentId } ?: allProfiles.firstOrNull()
        
        HomeUiState(
            energyScore = score,
            activeProfile = active,
            hasPermissions = perm,
            isHealthConnectAvailable = avail,
            wakeTime = wake,
            isLoading = loading
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    val permissions = healthConnectManager.requiredPermissions

    fun checkPermissions() {
        viewModelScope.launch {
            val avail = healthConnectManager.isAvailable()
            _isAvailable.value = avail
            if (avail) {
                _hasPermissions.value = healthConnectManager.hasAllPermissions()
            }
            refreshData()
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            if (_isLoading.value) return@launch
            _isLoading.value = true
            try {
                // Fetch wake time
                _wakeTime.value = circadianRepository.getWakeUpTime()
                
                // Fetch active profile and sync score
                val active = healthRepository.allProfilesFlow().first().firstOrNull()
                energyRepository.syncDailyScore(active)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(500) // Visual feedback
                _isLoading.value = false
            }
        }
    }

    fun setManualWakeTime(timestamp: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            if (timestamp == 0L) {
                circadianRepository.clearWakeTime()
            } else {
                circadianRepository.setManualWakeTime(timestamp)
            }
            refreshData()
        }
    }

    fun forceSync() {
        energyRepository.scheduleBackgroundSync(context)
        refreshData()
    }
}