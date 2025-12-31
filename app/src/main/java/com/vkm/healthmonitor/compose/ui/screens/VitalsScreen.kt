package com.vkm.healthmonitor.compose.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.core.model.ProfileWithVitals
import com.vkm.healthmonitor.compose.ui.components.ChartsForProfiles
import com.vkm.healthmonitor.compose.ui.components.ProfileSelector
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel
import com.vkm.healthmonitor.compose.viewmodel.VitalsViewModel
import kotlinx.coroutines.launch

@Composable
fun VitalsScreen(
    vitalsVm: VitalsViewModel = hiltViewModel(),
    profileVm: ProfileListViewModel = hiltViewModel()
) {
    val selected by profileVm.selectedProfile.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    if (selected != null) {
        val vitals by vitalsVm.getVitalsFlowForProfile(selected!!.id).collectAsState(emptyList())

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selected == null) {
                Text("Select a profile to add vitals.")
                ProfileSelector()
            } else {
                ProfileSelector()
                Text("Vitals for ${selected!!.name}", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = vitalsVm.pulse.value,
                    onValueChange = { vitalsVm.pulse.value = it },
                    label = { Text("Pulse (bpm)") })
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = vitalsVm.bpSys.value,
                        onValueChange = { vitalsVm.bpSys.value = it },
                        label = { Text("BP Systolic") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = vitalsVm.bpDia.value,
                        onValueChange = { vitalsVm.bpDia.value = it },
                        label = { Text("BP Diastolic") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = vitalsVm.temperature.value,
                    onValueChange = { vitalsVm.temperature.value = it },
                    label = { Text("Temperature °C") })
                OutlinedTextField(
                    value = vitalsVm.spo2.value,
                    onValueChange = { vitalsVm.spo2.value = it },
                    label = { Text("SpO₂ %") })

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        if (selected != null) {
                            vitalsVm.addVital(selected!!.id) { validation ->
                                scope.launch { snackbarHostState.showSnackbar(validation) }
                            }
//                            vitalsVm.clearVitals()

                        }
                    }) { Text("Save Vitals") }
                }

                Spacer(Modifier.height(12.dp))
                // ensure we load the vitals whenever selection changes
                LaunchedEffect(selected?.id) {
                    selected?.let { vitalsVm.loadVitals(it.id) }
                }

                // charts for the selected profile
                if (vitals.isNotEmpty()) {
                    // ensure at least two points per graph inside ChartsForProfiles
                    ChartsForProfiles(
                        listOf(
                            ProfileWithVitals(
                                profile = selected!!,
                                vitals = vitals
                            )
                        )
                    )
                }
            }
        }
    } else {
        Column {
            Text("Select a profile to add vitals.")
            ProfileSelector()
        }
    }
}

