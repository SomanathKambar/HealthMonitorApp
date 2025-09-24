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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.compose.ui.components.ProfileSelector
import com.vkm.healthmonitor.compose.viewmodel.HydrationViewModel
import com.vkm.healthmonitor.compose.viewmodel.ProfileListViewModel

@Composable
fun HydrationScreen(
    vm: HydrationViewModel = hiltViewModel(),
    profileVm: ProfileListViewModel = hiltViewModel()
) {
    val selected by profileVm.selectedProfile.collectAsState()
    val total by vm.todayTotal.collectAsState()
    val logs by vm.todayLogs.collectAsState()

    if (selected == null) {
        Column {
            Text("Select a profile to track hydration")
            ProfileSelector()
        }

    } else {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileSelector(profileVm)
            if (selected == null) {
                Text("Select a profile to track hydration")
            } else {

                LaunchedEffect(selected?.id) {
                    selected?.let { vm.observeFor(it.id) }
                }

                Text("${selected!!.name} - Hydration", style = MaterialTheme.typography.titleLarge)
                LinearProgressIndicator(
                    progress = (total.toFloat() / (selected!!.dailyWaterGoalMl.toFloat())).coerceIn(
                        0f,
                        1f
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                Text("$total ml / ${selected!!.dailyWaterGoalMl} ml")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { vm.addDrink(selected!!.id, 100) }) { Text("+100 ml") }
                    Button(onClick = { vm.addDrink(selected!!.id, 250) }) {
                        Icon(Icons.Filled.LocalDrink, contentDescription = null)
                        Spacer(Modifier.width(6.dp)); Text("+250 ml")
                    }
                    Button(onClick = { vm.addDrink(selected!!.id, 500) }) { Text("+500 ml") }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            // remove last:
                            // TODO: implement repo.deleteById in hydration repo and call here
                        }) { Text("- Last") }
                }

                Spacer(Modifier.height(8.dp))
                Text("Today's logs:")
                Column(Modifier.fillMaxWidth()) {
                    logs.forEach { l ->
                        Text(
                            "${l.amountMl} ml @ ${
                                java.text.SimpleDateFormat("HH:mm")
                                    .format(java.util.Date(l.timestamp))
                            }"
                        )
                    }
                }
            }
        }
    }
}
