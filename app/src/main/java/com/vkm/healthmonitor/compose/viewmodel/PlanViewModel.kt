package com.vkm.healthmonitor.compose.viewmodel

import com.vkm.healthmonitor.compose.data.model.HealthPlan
import com.vkm.healthmonitor.compose.data.repository.PlanRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(private val repo: PlanRepository): ViewModel() {
    fun plansForProfile(profileId: Int) = repo.plansForProfileFlow(profileId)
    fun addPlan(plan: HealthPlan) = viewModelScope.launch { repo.insertPlan(plan) }
    fun deletePlan(id: Long) = viewModelScope.launch { repo.deletePlan(id) }
    fun refreshTemplates() = viewModelScope.launch { repo.refreshPlansFromServer() }
}
