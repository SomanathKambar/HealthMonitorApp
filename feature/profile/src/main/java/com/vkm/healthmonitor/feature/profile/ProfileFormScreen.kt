package com.vkm.healthmonitor.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vkm.healthmonitor.core.common.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    nav: NavController, 
    vm: ProfileFormViewModel = hiltViewModel(),
    paddingValues: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
) {
    val state by vm.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // ... (LaunchedEffect remains same)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("My Metabolic Identity", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Personal Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(), 
                value = state.name, 
                onValueChange = { vm.onNameChange(it) }, 
                label = { Text("Display Name") },
                shape = MaterialTheme.shapes.medium
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f), 
                    value = state.age, 
                    onValueChange = { vm.onAgeChange(it) }, 
                    label = { Text("Age") },
                    shape = MaterialTheme.shapes.medium
                )
                // Gender dropdown
                var genderExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox (
                    expanded = genderExpanded, 
                    onExpandedChange = { genderExpanded = !genderExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = state.gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(genderExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                    ExposedDropdownMenu(expanded = genderExpanded, onDismissRequest = { genderExpanded = false }) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    vm.updateField { it.copy(gender = option) }
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(modifier = Modifier.weight(1f), value = state.height, onValueChange = { vm.onHeightChange(it) }, label = { Text("Height (cm)") })
                OutlinedTextField(modifier = Modifier.weight(1f), value = state.weight, onValueChange = { vm.onWeightChange(it) }, label = { Text("Weight (kg)") })
            }
            
            Spacer(Modifier.height(8.dp))
            Text("Performance Goals", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = state.dailyStepGoal, onValueChange = { vm.onStepGoalChange(it) }, label = { Text("Daily Step Target") })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = state.dailySleepGoal, onValueChange = { vm.onSleepGoalChange(it) }, label = { Text("Ideal Sleep (Hours)") })

            // Caffeine Sensitivity dropdown
            var caffeineExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox (expanded = caffeineExpanded, onExpandedChange = { caffeineExpanded = !caffeineExpanded }) {
                OutlinedTextField(
                    value = state.caffeineSensitivity,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Caffeine Sensitivity") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(caffeineExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = caffeineExpanded, onDismissRequest = { caffeineExpanded = false }) {
                    listOf("Low", "Medium", "High").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vm.onCaffeineChange(option)
                                caffeineExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                onClick = {
                    if (state.name.isBlank()) {
                        coroutineScope.launch { snackbarHostState.showSnackbar("Name is required") }
                    } else {
                        vm.saveProfile()
                        nav.navigate(Screen.EnergyDashboard.route) {
                            popUpTo(Screen.EnergyDashboard.route) { inclusive = true }
                        }
                    }
                },
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Filled.Save, contentDescription = "Save")
                Spacer(Modifier.width(8.dp))
                Text("Save Metabolic Identity", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}