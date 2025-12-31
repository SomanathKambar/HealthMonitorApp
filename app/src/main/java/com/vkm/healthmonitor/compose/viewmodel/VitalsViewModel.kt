package com.vkm.healthmonitor.compose.viewmodel


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.core.model.HealthStandard
import com.vkm.healthmonitor.core.model.VitalEntry
import com.vkm.healthmonitor.core.data.repository.HealthRepository
import com.vkm.healthmonitor.core.data.repository.ProfileRepository
import com.vkm.healthmonitor.core.data.repository.VitalRepository
import com.vkm.healthmonitor.core.common.validator.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VitalsViewModel @Inject constructor(
    private val vitalRepo: VitalRepository,
    private val profileRepo: ProfileRepository,
    private val healthRepository: HealthRepository
) : ViewModel() {

    val currentProfileId = profileRepo.currentProfileIdFlow().stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // vitals flow depends on current profile
    val vitals: StateFlow<List<VitalEntry>> =
        currentProfileId.filterNotNull().flatMapLatest { id ->
            if (id == 0) flowOf(emptyList())
            else vitalRepo.getVitalsForProfile(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun getVitalsFlowForProfile(profileId: Int): Flow<List<VitalEntry>> = vitalRepo.getVitalsForProfile(profileId)

    fun addVitalForCurrentProfile(v: VitalEntry) = viewModelScope.launch {
        val pid = currentProfileId.value
        if (pid != 0 && pid != null) vitalRepo.insertVital(v.copy(profileId = pid))
    }

    fun plausible(v: VitalEntry) = Validator.plausible(v)

    fun interpret(v: VitalEntry, standards: List<HealthStandard>) = Validator.interpret(v, standards)


    var pulse =  mutableStateOf<String>("")
    var bpSys = mutableStateOf<String>("")
    var bpDia = mutableStateOf<String>("")
    var temperature = mutableStateOf<String>("")
    var spo2 = mutableStateOf<String>("")

    private val _vitalsForSelected = MutableStateFlow<List<VitalEntry>>(emptyList())
    val vitalsForSelected: StateFlow<List<VitalEntry>> = _vitalsForSelected

    fun addVital(profileId: Int,  onValidation: (String) -> Unit) {
        viewModelScope.launch {
            val entry = VitalEntry(
                profileId = profileId,
                pulse = pulse.value.toIntOrNull() ?: 0,
                bpSys = bpSys.value.toIntOrNull() ?: 0,
                bpDia = bpDia.value.toIntOrNull() ?: 0,
                temperature = temperature.value.toFloatOrNull() ?: 0f,
                spo2 = spo2.value.toIntOrNull() ?: 0,
                timestamp = System.currentTimeMillis()
            )
            vitalRepo.insertVital(entry)
            _vitalsForSelected.value = vitalRepo.getVitalsForProfile(profileId).firstOrNull() ?: emptyList()
            val result = Validator.interpret(entry, healthRepository.standardsFlow().first())
            onValidation(result.toString())
        }
    }

    fun loadVitals(profileId: Int) {
        viewModelScope.launch {
            _vitalsForSelected.value = vitalRepo.getVitalsForProfile(profileId).firstOrNull()?: emptyList()
        }
    }

    fun clearVitals() {
        pulse.value = ""
        bpSys.value = ""
        bpDia.value = ""
        temperature.value = ""
        spo2.value = ""
    }
}
