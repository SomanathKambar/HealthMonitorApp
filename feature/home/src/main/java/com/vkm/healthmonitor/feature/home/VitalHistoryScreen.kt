package com.vkm.healthmonitor.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import com.vkm.healthmonitor.core.designsystem.components.AnimatedBatteryIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalHistoryScreen(
    nav: NavController,
    viewModel: VitalHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm").withZone(ZoneId.systemDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Metabolic History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.seedData() }) {
                        Text("Mock Data", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                if (state.energyScores.isNotEmpty()) {
                    Text("Bio-Energy Trend (7 Days)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            state.energyScores.forEach { score ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(), 
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(score.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${score.score}%", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(12.dp))
                                        AnimatedBatteryIcon(level = score.score, modifier = Modifier.size(36.dp, 18.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No history detected yet.", style = MaterialTheme.typography.titleSmall)
                            Text("Use 'Recalculate Energy' on dashboard to sync with Health Connect.", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                
                Text("Metabolic Log Book", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Track your body's response to your daily protocol.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
            }

            if (state.vitals.isEmpty()) {
                item {
                    Text("No logs found. Tap 'Adjust' on dashboard to start.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(state.vitals) { vital ->
                    VitalLogCard(vital, dateFormatter)
                }
            }
        }
    }
}

@Composable
fun VitalLogCard(vital: com.vkm.healthmonitor.core.model.VitalEntry, formatter: DateTimeFormatter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = formatter.format(Instant.ofEpochMilli(vital.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "STATUS: CHECKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                VitalValueItem("Pulse", "${vital.pulse}", "bpm", Modifier.weight(1f))
                VitalValueItem("BP", "${vital.bpSys}/${vital.bpDia}", "mmHg", Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                VitalValueItem("Temp", "${vital.temperature}", "°C", Modifier.weight(1f))
                VitalValueItem("SpO₂", "${vital.spo2}", "%", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun VitalValueItem(label: String, value: String, unit: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.width(2.dp))
            Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
