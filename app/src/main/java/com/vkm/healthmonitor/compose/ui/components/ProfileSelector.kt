package com.vkm.healthmonitor.compose.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelector(
    vm: ProfileListViewModel = hiltViewModel()
) {
    val profiles by vm.profiles.collectAsState()
    val selected by vm.selectedProfile.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected?.name ?: "Select Profile",
            onValueChange = {},
            readOnly = true,
            label = { Text("Profile") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            profiles.forEach { p ->
                DropdownMenuItem(
                    text = { Text(p.name) },
                    onClick = {
                        vm.selectProfile(p.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
