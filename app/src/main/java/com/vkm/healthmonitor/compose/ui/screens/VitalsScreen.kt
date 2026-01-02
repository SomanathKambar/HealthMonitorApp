package com.vkm.healthmonitor.compose.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.core.model.ProfileWithVitals
import com.vkm.healthmonitor.core.designsystem.components.ChartsForProfiles
import com.vkm.healthmonitor.core.designsystem.components.ProfileSelector
import com.vkm.healthmonitor.feature.profile.ProfileListViewModel
import com.vkm.healthmonitor.compose.viewmodel.VitalsViewModel
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalContext
import com.vkm.healthmonitor.core.common.BiometricHelper
import com.vkm.healthmonitor.core.common.BiometricLauncher
import androidx.fragment.app.FragmentActivity

@Composable
fun VitalsScreen(
    vitalsVm: VitalsViewModel = hiltViewModel(),
    profileVm: ProfileListViewModel = hiltViewModel()
) {
    val profiles by profileVm.profiles.collectAsState()
    val selected by profileVm.selectedProfile.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val biometricHelper = remember { BiometricHelper(context) }

    val authLauncher = remember(context) {
        BiometricLauncher(
            activity = context as FragmentActivity,
            onAuthenticationSucceeded = {
                vitalsVm.addVital(selected!!.id) { validation ->
                    scope.launch { snackbarHostState.showSnackbar(validation) }
                }
            },
            onAuthenticationError = { _, errString ->
                scope.launch { snackbarHostState.showSnackbar(errString) }
            },
            onAuthenticationFailed = {
                scope.launch { snackbarHostState.showSnackbar("Authentication failed") }
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileSelector(
            profiles = profiles,
            selectedProfile = selected,
            onProfileSelected = { profileVm.selectProfile(it) }
        )

        if (selected != null) {
            val vitals by vitalsVm.getVitalsFlowForProfile(selected!!.id).collectAsState(emptyList())

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
                        if (biometricHelper.canAuthenticate()) {
                            authLauncher.authenticate(
                                biometricHelper.createPromptInfo(
                                    title = "Authorize Vital Entry",
                                    subtitle = "Confirm your identity to save vitals"
                                )
                            )
                        } else {
                            vitalsVm.addVital(selected!!.id) { validation ->
                                scope.launch { snackbarHostState.showSnackbar(validation) }
                            }
                        }
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
                ChartsForProfiles(
                    listOf(
                        ProfileWithVitals(
                            profile = selected!!,
                            vitals = vitals
                        )
                    )
                )
            }
        } else {
            Text("Select a profile to add vitals.")
        }
    }
}