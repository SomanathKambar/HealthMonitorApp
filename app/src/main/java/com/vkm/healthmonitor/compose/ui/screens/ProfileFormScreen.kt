package com.vkm.healthmonitor.compose.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vkm.healthmonitor.compose.viewmodel.ProfileFormViewModel
import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileFormScreen(
//    nav: NavController,
//    vm: ProfileFormViewModel = hiltViewModel(),
//    profileId: Int? = null
//) {
//    val state by vm.uiState.collectAsState()
//    val snackbarHostState = remember { SnackbarHostState() }
//    val coroutineScope = rememberCoroutineScope()
//
//    LaunchedEffect(profileId) {
//        if (profileId == null) vm.reset() else vm.loadProfile(profileId)
//    }
//
//    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
//        Column(
//            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            Text(if (profileId == null) "Add Profile" else "Edit Profile",
//                style = MaterialTheme.typography.titleLarge)
//
//            OutlinedTextField(state.name, { value -> vm.updateField { it.copy(name = value) } }, label = { Text("Name*") })
//            OutlinedTextField(state.age, {  value -> vm.updateField { it.copy(age = value ) } }, label = { Text("Age*") })
//
//            // ✅ Gender Dropdown
//            var expanded by remember { mutableStateOf(false) }
//            ExposedDropdownMenuBox (expanded = expanded, onExpandedChange = { expanded = !expanded }) {
//                OutlinedTextField(
//                    value = state.gender,
//                    onValueChange = {},
//                    readOnly = true,
//                    label = { Text("Gender*") },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
//                    modifier = Modifier.menuAnchor().fillMaxWidth()
//                )
//                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//                    listOf("Male", "Female", "Other").forEach { option ->
//                        DropdownMenuItem(
//                            text = { Text(option) },
//                            onClick = {
//                                vm.updateField { it.copy(gender = option) }
//                                expanded = false
//                            }
//                        )
//                    }
//                }
//            }
//
//            OutlinedTextField(state.height, { value -> vm.updateField { it.copy(height = value) } }, label = { Text("Height (cm)*") })
//            OutlinedTextField(state.weight, { value ->  vm.updateField { it.copy(weight = value) } }, label = { Text("Weight (kg)*") })
//            OutlinedTextField(state.waterGoal, { value -> vm.updateField { it.copy(waterGoal = value) } }, label = { Text("Daily Water Goal") })
//
//            Spacer(Modifier.height(12.dp))
//
//            Button(onClick = {
//                if (state.name.isBlank() || state.age.isBlank() || state.height.isBlank() || state.weight.isBlank()) {
//                    coroutineScope.launch { snackbarHostState.showSnackbar("Fill required fields") }
//                } else {
//                    vm.saveProfile()
//                    nav.popBackStack()
//                }
//            }) { Text("Save") }
//        }
//    }
//}

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import com.vkm.healthmonitor.compose.ui.navigation.Screen
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(nav: NavController, vm: ProfileFormViewModel = hiltViewModel(), profileId: String? = null) {
    val state by vm.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (profileId == null) "Add Profile" else "Edit Profile", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = state.name, onValueChange = { vm.onNameChange(it) }, label = { Text("Name*") })
            OutlinedTextField(value = state.age, onValueChange = { vm.onAgeChange(it) }, label = { Text("Age*") })

            // Gender dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox (expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = state.gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender*") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Male", "Female", "Other").forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                vm.updateField { it.copy(gender = option) }
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = state.height, onValueChange = { vm.onHeightChange(it) }, label = { Text("Height (cm)*") })
            OutlinedTextField(value = state.weight, onValueChange = { vm.onWeightChange(it) }, label = { Text("Weight (kg)*") })
            OutlinedTextField(value = state.waterGoal, onValueChange = { vm.onWaterGoalChange(it) }, label = { Text("Daily Water Goal (ml)") })

            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (state.name.isBlank() || state.age.isBlank() || state.height.isBlank() || state.weight.isBlank()) {
                    coroutineScope.launch { snackbarHostState.showSnackbar("Please fill required fields") }
                } else {
                vm.saveProfile()
                nav.navigate(Screen.ProfileList.route)
                }
            }) {
                Icon(Icons.Filled.Save, contentDescription = "Save")
                Spacer(Modifier.width(8.dp))
                Text("Save")
            }
        }
    }
}
