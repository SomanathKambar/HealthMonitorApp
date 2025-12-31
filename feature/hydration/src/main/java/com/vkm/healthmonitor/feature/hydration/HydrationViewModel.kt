package com.vkm.healthmonitor.feature.hydration


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.core.model.HydrationLog
import com.vkm.healthmonitor.core.data.repository.HydrationRepository
import com.vkm.healthmonitor.core.data.repository.ProfileRepository
import com.vkm.healthmonitor.core.model.HydrationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val repo: HydrationRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

//    private val _todayTotal = MutableStateFlow(0)
//    val todayTotal: StateFlow<Int> = _todayTotal
//
//    private val _todayLogs = MutableStateFlow<List<HydrationLog>>(emptyList())
//    val todayLogs: StateFlow<List<HydrationLog>> = _todayLogs
//
//    private var collectJob: Job? = null
//
//    fun observeFor(profileId: Int) {
//        collectJob?.cancel()
//        collectJob = viewModelScope.launch {
//            repo.todayTotalFlow(profileId).collectLatest { total ->
//                _todayTotal.value = total
//            }
//        }
//        // separate flow for logs
//        collectJob = viewModelScope.launch {
//            repo.todayLogsFlow(profileId).collectLatest { logs -> _todayLogs.value = logs }
//        }
//    }
//
//    fun addDrink(profileId: Int, amountMl: Int) = viewModelScope.launch {
//        repo.insertHydration(profileId = profileId, amountMl = amountMl)
//    }

    private val _todayTotal = MutableStateFlow(0)
    val todayTotal: StateFlow<Int> = _todayTotal

    private val _todayLogs = MutableStateFlow<List<HydrationLog>>(emptyList())
    val todayLogs: StateFlow<List<HydrationLog>> = _todayLogs

    // For feedback
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error

    private var logsJob: Job? = null
    private var totalJob: Job? = null

    fun observeFor(profileId: Int) {
        logsJob?.cancel(); totalJob?.cancel()

        totalJob = viewModelScope.launch {
            repo.todayTotalFlow(profileId).collect { total -> _todayTotal.value = total }
        }
        logsJob = viewModelScope.launch {
            repo.todayLogsFlow(profileId).collect { logs -> _todayLogs.value = logs }
        }
    }

    fun addDrink(profileId: Int, amountMl: Int) = viewModelScope.launch {
        when (val res = repo.tryAddHydration(profileId, amountMl)) {
            is HydrationResult.Success -> { /* OK, UI will update via flows */ }
            is HydrationResult.ExceedsSafe -> {
                _error.emit("Cannot add: would exceed safe limit of ${res.safeCap} ml for today.")
            }
            is HydrationResult.Error -> {
                _error.emit("Error: ${res.reason}")
            }
        }
    }

    fun removeLast(profileId: Int) = viewModelScope.launch {
        try {
            repo.removeLastHydration(profileId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
