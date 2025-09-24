package com.vkm.healthmonitor.compose.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vkm.healthmonitor.compose.ui.navigation.Screen
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
//@Composable
//fun ProfileListScreen(
//    nav: NavController,
//    vm: ProfileListViewModel = hiltViewModel()
//) {
//    val profiles by vm.profiles.collectAsState()
//
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text("Profiles", style = MaterialTheme.typography.titleLarge)
//            Button(onClick = {
//                nav.navigate(Screen.ProfileForm.route) // no id -> add mode
//            }) { Text("Add") }
//        }
//
//        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(profiles) { p ->
//                Card(Modifier.fillMaxWidth()) {
//                    Row(
//                        Modifier.padding(12.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Column(Modifier.weight(1f).clickable { vm.selectProfile(p.id) }) {
//                            Text(p.name, style = MaterialTheme.typography.titleMedium)
//                            Text("Relation: ${p.relationTo ?: "Self"}")
//                            Text("BMI: %.1f".format(p.bmi))
//                        }
//                        Row {
//                            IconButton(onClick = {
//                                nav.navigate("${Screen.ProfileForm.route}?id=${p.id}")
//                            }) {
//                                Icon(Icons.Filled.Edit, "Edit")
//                            }
//                            IconButton(onClick = { vm.deleteProfile(p) }) {
//                                Icon(Icons.Filled.Delete, "Delete")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.vkm.healthmonitor.compose.ui.components.ProfileSelector
import com.vkm.healthmonitor.compose.viewmodel.ProfileFormViewModel


@Composable
fun ProfileListScreen(nav: NavController, vm: ProfileListViewModel = hiltViewModel(), profileFormViewModel: ProfileFormViewModel = hiltViewModel()) {
    val profiles by vm.profiles.collectAsState()
    if (profiles.isEmpty()) {
        Box(Modifier.fillMaxSize() .padding(16.dp)) {
                IconButton(modifier = Modifier.fillMaxSize(1f).align(alignment = Alignment.Center), onClick = { nav.navigate(Screen.ProfileForm.route) {
                    popUpTo(Screen.ProfileList.route); launchSingleTop = true
                } }) {
                    Column(Modifier.align(Alignment.Center)) {
                    Icon(Icons.Filled.Add, contentDescription = "Add",  modifier = Modifier.wrapContentSize().align(alignment = Alignment.CenterHorizontally))
                    Text("Add Profile", style = MaterialTheme.typography.titleLarge, modifier = Modifier.wrapContentSize().align(alignment = Alignment.CenterHorizontally))
                    }
                }
        }
    } else {
        Column(Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Profiles", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { nav.navigate(Screen.ProfileForm.route)}) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(profiles) { p ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier
                                .weight(1f)
                                .clickable { vm.selectProfileById(p.id) }) {
                                Text(p.name, style = MaterialTheme.typography.titleMedium)
                                Text("BMI: %.1f".format(p.bmi))
                            }
                            Row {
//                                IconButton(onClick = {
//                                    profileFormViewModel.selectProfile(p.id)
//                                    nav.navigate("${Screen.ProfileForm.route}?id=${p.id}")
//                                }) {
//                                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
//                                }
                                IconButton(onClick = { vm.deleteProfile(p) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
