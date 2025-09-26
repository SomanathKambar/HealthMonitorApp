package com.vkm.healthmonitor.compose.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkm.healthmonitor.compose.data.model.Profile
import com.vkm.healthmonitor.compose.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileListViewModel @Inject constructor(
    private val repo: ProfileRepository
): ViewModel() {
    init {

    }
    val profiles = repo.allProfilesFlow().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val profilesWithVitals = repo.getProfilesWithVitalsFlow().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addProfile(p: Profile) = viewModelScope.launch { repo.insertOrUpdate(p) }
//    fun deleteProfile(p: Profile) = viewModelScope.launch { repo.deleteProfile(p) }

    fun refreshFromServer() = viewModelScope.launch { repo.refreshProfilesFromServer() }

//    private val _selectedProfile = MutableStateFlow<Profile?>(null)
//    val selectedProfile: StateFlow<Profile?> = _selectedProfile

    fun selectProfile(id: Int) {
        viewModelScope.launch {
            val profile = repo.getById(id)
            _selectedProfile.value = profile
        }
    }

//    fun clearSelection() {
//        _selectedProfile.value = null
//    }

//    val profiles: StateFlow<List<Profile>> =
//        repo.getProfilesFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

//    val profilesWithVitals: StateFlow<List<ProfileWithVitals>> =
//        repo.getProfilesWithVitalsFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _selectedProfile = MutableStateFlow<Profile?>(null)
    val selectedProfile: StateFlow<Profile?> = _selectedProfile.asStateFlow()

    fun selectProfileById(id: Int) {
        viewModelScope.launch {
            _selectedProfile.value = repo.getById(id)
        }
    }

    fun clearSelection() { _selectedProfile.value = null }

    fun deleteProfile(p: Profile) {
        viewModelScope.launch { repo.deleteProfile(p); if (_selectedProfile.value?.id == p.id) _selectedProfile.value = null }
    }
}
