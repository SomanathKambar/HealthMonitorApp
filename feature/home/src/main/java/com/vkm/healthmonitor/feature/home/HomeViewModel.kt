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
    val dynamicBattery: Int = 0,
    val activeProfile: Profile? = null,
    val hasPermissions: Boolean = false,
    val isHealthConnectAvailable: Boolean = true,
    val wakeTime: Instant? = null,
    val isLoading: Boolean = false,
    val recoveryValue: Float = 60f,
    val strainValue: Float = 40f
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

    // Reactive UI State derived from core flows
    val uiState: StateFlow<HomeUiState> = combine(
        energyRepository.latestScore,
        healthRepository.allProfilesFlow(),
        healthRepository.currentProfileIdFlow(),
        _hasPermissions,
        _isAvailable,
        _wakeTime,
        _isLoading
    ) { args ->
        val score = args[0] as DailyEnergyScore?
        val allProfiles = args[1] as List<Profile>
        val currentId = args[2] as Int?
        val perm = args[3] as Boolean
        val avail = args[4] as Boolean
        val wake = args[5] as java.time.Instant?
        val loading = args[6] as Boolean

        val active = allProfiles.find { it.id == currentId } ?: allProfiles.firstOrNull()
        
        // Pass essential data forward to a suspendable transformation
        active to score to wake to loading to perm to avail
    }.flatMapLatest { data ->
        val (p_s_w_l_p, avail) = data
        val (p_s_w_l, perm) = p_s_w_l_p
        val (p_s_w, loading) = p_s_w_l
        val (p_s, wake) = p_s_w
        val (active, score) = p_s
        
        flow {
            val battery = energyRepository.getDynamicEnergyScore(score, wake)
            
            // Calculate detailed balance incorporating manual adjustments for today
            val startOfToday = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            // We need a way to get manual adjustments. I'll use the energyRepository if possible or a direct DB call.
            // For now, I'll calculate it based on the latest baseline.
            
            val baseRecovery = score?.sleepScore?.toFloat() ?: 60f
            val baseStrain = score?.activityBalanceScore?.toFloat() ?: 40f
            
            emit(HomeUiState(
                energyScore = score,
                dynamicBattery = battery,
                activeProfile = active,
                hasPermissions = perm,
                isHealthConnectAvailable = avail,
                wakeTime = wake,
                isLoading = loading,
                recoveryValue = baseRecovery, // In a full implementation, we'd sum adjustments here
                strainValue = baseStrain
            ))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            // energyRepository.seedHistoryIfEmpty() // Disabled to show only real data
        }
    }

    val permissions = healthConnectManager.requiredPermissions

    fun adjustEnergy(type: String, change: Int, note: String) {
        viewModelScope.launch {
            energyRepository.adjustEnergy(type, change, note)
            refreshData()
        }
    }

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
            // Only skip if already loading to prevent concurrent refreshes
            // but we need to be careful how it's called.
            _isLoading.value = true
            try {
                // Fetch wake time
                _wakeTime.value = circadianRepository.getWakeUpTime()
                
                // Fetch active profile and sync score
                val activeProfiles = healthRepository.allProfilesFlow().first()
                val currentId = healthRepository.currentProfileIdFlow().first()
                val active = activeProfiles.find { it.id == currentId } ?: activeProfiles.firstOrNull()
                
                energyRepository.syncDailyScore(active)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(800) // Visual feedback for the "optimizing" feel
                _isLoading.value = false
            }
        }
    }

    fun setManualWakeTime(timestamp: Long) {
        viewModelScope.launch {
            try {
                if (timestamp == 0L) {
                    circadianRepository.clearWakeTime()
                } else {
                    circadianRepository.setManualWakeTime(timestamp)
                }
                // Don't set _isLoading here, let refreshData handle the lifecycle
                refreshData()
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun forceSync() {
        energyRepository.scheduleBackgroundSync(context)
        refreshData()
    }
}