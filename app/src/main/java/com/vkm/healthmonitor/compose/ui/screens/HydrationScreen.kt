package com.vkm.healthmonitor.compose.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.core.common.validator.HydrationLogic
import com.vkm.healthmonitor.core.designsystem.components.ProfileSelector
import com.vkm.healthmonitor.compose.viewmodel.HydrationViewModel
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HydrationScreen(
    vm: HydrationViewModel = hiltViewModel(),
    profileVm: ProfileListViewModel = hiltViewModel()
) {
    val profiles by profileVm.profiles.collectAsState()
    val selected by profileVm.selectedProfile.collectAsState()
    val total by vm.todayTotal.collectAsState()
    val logs by vm.todayLogs.collectAsState()
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        vm.error.collect { msg ->
            scaffoldState.snackbarHostState.showSnackbar(msg)
        }
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

        if (selected == null) {
            Text("Select a profile to track hydration")
        } else {
            Scaffold(scaffoldState = scaffoldState) { pad ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(pad),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LaunchedEffect(selected!!.id) {
                        vm.observeFor(selected!!.id)
                    }

                    val goal = HydrationLogic.computeDailyGoal(selected!!)
                    val safeMax = HydrationLogic.maxSafeIntake(goal)

                    Text(
                        "${selected!!.name} - Hydration",
                        style = MaterialTheme.typography.titleLarge
                    )
                    LinearProgressIndicator(
                        progress = (total.toFloat() / goal.toFloat()).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                    Text("$total ml / $goal ml")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(100, 250, 500).forEach { amt ->
                            val will = total + amt
                            val enabled = will <= safeMax
                            Button(
                                onClick = { vm.addDrink(selected!!.id, amt) },
                                enabled = enabled
                            ) {
                                Icon(Icons.Filled.LocalDrink, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("+${amt} ml")
                            }
                        }
                        Button(
                            onClick = { vm.removeLast(selected!!.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("- Last")
                        }
                    }

                    if (total >= goal) {
                        Text("🎉 You’ve reached your hydration goal today!", color = Color.Green)
                    } else if (total + 100 > safeMax) {
                        Text(
                            "⚠️ You are close to the safe limit (${safeMax} ml). Be cautious!",
                            color = Color.Red
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Today's logs:")
                    Column(Modifier.fillMaxWidth()) {
                        logs.forEach { l ->
                            val time = SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(l.timestamp))
                            Text("${l.amountMl} ml @ $time")
                        }
                    }
                }
            }
        }
    }
}