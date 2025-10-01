package com.vkm.healthmonitor.compose.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.compose.ui.components.ChartsForProfiles
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartsScreen(
    profileVm: ProfileListViewModel = hiltViewModel()
) {
    val profilesWithVitals by profileVm.profilesWithVitals.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Charts", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { /* maybe export or refresh */ }) { Text("Refresh") }
        }
        ChartsForProfiles(profilesWithVitals)
        Spacer(Modifier.height(34.dp))
    }
}

//@Composable
//fun ChartsScreen(
//    profileVm: ProfileListViewModel = hiltViewModel(),
//    vitalsVm: VitalsViewModel = hiltViewModel()
//) {
//    val selected by profileVm.selectedProfile.collectAsState()
//    val profilesWithVitals by profileVm.profilesWithVitals.collectAsState()
//    if(selected == null)  {
//        ProfileSelector()
//    } else {
//    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//        Spacer(Modifier.height(8.dp))
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text("Charts", style = MaterialTheme.typography.titleLarge)
//            Button(onClick = { /* maybe export or refresh */ }) { Text("Refresh") }
//        }
//
//        if (profilesWithVitals.isEmpty()) {
//            Text("No profiles or vitals yet. Add a profile to start tracking.")
//        } else {
//
//        // if selected show single person charts, else show all`
//        if (selected != null) {
//            // load vitals for selected (ensures the VitalsViewModel collects)
//            LaunchedEffect(selected?.id) { selected?.let { vitalsVm.loadVitals(it.id) } }
//            val vitals = vitalsVm.vitals.collectAsState().value
//            ChartsForProfiles(listOf(ProfileWithVitals(profile = selected!!, vitals = vitals)))
//        } else {
//            ChartsForProfiles(profilesWithVitals)
//        }
//        }
//    }
//    }
//}
