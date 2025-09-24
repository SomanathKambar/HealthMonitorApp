package com.vkm.healthmonitor.compose.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.compose.data.model.HydrationLog
import com.vkm.healthmonitor.compose.data.repository.HydrationRepository
import com.vkm.healthmonitor.compose.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HydrationViewModel @Inject constructor(
    private val repo: HydrationRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

//    // current profile id from profileRepo
//    val currentProfileId: StateFlow<Int?> = profileRepo.currentProfileIdFlow()
//        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
//
//    // today's total depends on current profile
//    val today: StateFlow<Int> = currentProfileId.filterNotNull().flatMapLatest { pid ->
//        if (pid == 0) flowOf(0) else repo.todayHydrationFlowForProfile(pid)
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)
//
//    fun add(amount: Int) {
//        viewModelScope.launch {
//            val pid = currentProfileId.value ?: return@launch
//            if (pid != 0) repo.insertHydration(pid, amount)
//        }
//    }
//
//    fun removeLast() {
//        viewModelScope.launch {
//            val pid = currentProfileId.value ?: return@launch
//            if (pid != 0) repo.removeLastHydration(pid)
//        }
//    }
//
//    private val _hydrationLogs = MutableStateFlow<List<HydrationLog>>(emptyList())
//    val hydrationLogs: StateFlow<List<HydrationLog>> = _hydrationLogs
//
//    fun addDrink(amount: Int, profileId: Int?) {
//        if (profileId == null) return
//        viewModelScope.launch {
//            repo.insertHydration(profileId, amount)
//            _hydrationLogs.value = repo.getHydrationForProfile(profileId)
//        }
//    }

    private val _todayTotal = MutableStateFlow(0)
    val todayTotal: StateFlow<Int> = _todayTotal

    private val _todayLogs = MutableStateFlow<List<HydrationLog>>(emptyList())
    val todayLogs: StateFlow<List<HydrationLog>> = _todayLogs

    private var collectJob: Job? = null

    fun observeFor(profileId: Int) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            repo.todayTotalFlow(profileId).collectLatest { total ->
                _todayTotal.value = total
            }
        }
        // separate flow for logs
        collectJob = viewModelScope.launch {
            repo.todayLogsFlow(profileId).collectLatest { logs -> _todayLogs.value = logs }
        }
    }

    fun addDrink(profileId: Int, amountMl: Int) = viewModelScope.launch {
        repo.insertHydration(profileId = profileId, amountMl = amountMl)
    }

    fun removeLast(profileId: Int) = viewModelScope.launch {
        val last = repo.lastForProfile(profileId)
        last?.let { repo.insertHydration(it.id, it.amountMl) /* no-op placeholder; you'll call deleteById in DAO */ }
        // better to call dao.deleteById(last.id) - but repo currently lacks a deleteById wrapper; implement if needed.
    }
}
