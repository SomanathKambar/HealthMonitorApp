package com.vkm.healthmonitor.feature.hydration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vkm.healthmonitor.core.common.validator.HydrationLogic
import com.vkm.healthmonitor.feature.profile.ProfileListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HydrationScreen(
    vm: HydrationViewModel = hiltViewModel(),
    profileVm: ProfileListViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val profiles by profileVm.profiles.collectAsState()
    val soloProfile = profiles.firstOrNull() // SOLO MODE: Always take the first profile
    val total by vm.todayTotal.collectAsState()
    val logs by vm.todayLogs.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Metabolic Fueling", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        if (soloProfile == null) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text("Please set up your profile first to track hydration goals.", modifier = Modifier.padding(16.dp))
            }
        } else {
            LaunchedEffect(soloProfile.id) {
                vm.observeFor(soloProfile.id)
            }

            val goal = soloProfile.dailyWaterGoalMl
            val safeMax = goal + 1000

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Daily Efficiency Target", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Box(contentAlignment = Alignment.Center) {
                        LinearProgressIndicator(
                            progress = (total.toFloat() / goal.toFloat()).coerceIn(0f, 1f),
                            modifier = Modifier.fillMaxWidth().height(32.dp),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text("${total} / ${goal} ml", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(250, 500).forEach { amt ->
                    Button(
                        modifier = Modifier.weight(1f).height(56.dp),
                        onClick = { vm.addDrink(soloProfile.id, amt) },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.LocalDrink, null)
                        Spacer(Modifier.width(4.dp))
                        Text("+${amt}")
                    }
                }
            }
            
            OutlinedButton(
                onClick = { vm.removeLast(soloProfile.id) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Undo Last Entry")
            }

            Text("Metabolic Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            if (logs.isEmpty()) {
                Text("No intake logged yet. Stay hydrated to maintain energy.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            logs.reversed().forEach { l ->
                val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(l.timestamp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${l.amountMl} ml", fontWeight = FontWeight.Bold)
                        Text(time, color = Color.Gray)
                    }
                }
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}