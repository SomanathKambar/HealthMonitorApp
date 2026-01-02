package com.vkm.healthmonitor.feature.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.health.connect.client.PermissionController
import com.vkm.healthmonitor.core.designsystem.components.BodyBatteryGauge
import com.vkm.healthmonitor.core.designsystem.components.PieChartView
import com.vkm.healthmonitor.core.designsystem.theme.EnergyBlue
import com.vkm.healthmonitor.core.designsystem.theme.EnergyGreen
import com.vkm.healthmonitor.core.designsystem.theme.EnergyRed
import com.vkm.healthmonitor.core.designsystem.theme.EnergyYellow
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnergyDashboardScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    paddingValues: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHydration: () -> Unit = {},
    onNavigateToLightProtocol: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    val hcPermissions = viewModel.permissions

    val requestPermissions = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(hcPermissions)) {
            viewModel.refreshData()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkPermissions()
    }

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 0. Active Profile Header
            Card(
                onClick = onNavigateToProfile,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.activeProfile?.name ?: "Solo Metabolic Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Daily Goal: ${state.activeProfile?.dailyStepGoal ?: 10000} steps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Settings")
                }
            }

            // 1. Loading Indicator
            if (state.isLoading) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Recalculating metabolic data...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // 2. Health Connect Status
            if (!state.isHealthConnectAvailable && state.wakeTime != null) {
                Text(
                    "Solo Intentional Mode Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else if (state.isHealthConnectAvailable && !state.hasPermissions) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Connect Wearable Data", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Enable sync for higher resolution energy scores.", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { requestPermissions.launch(hcPermissions) }) {
                            Text("Connect")
                        }
                    }
                }
            }

            // 3. Morning Check-in
            if (state.wakeTime == null && !state.isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("☀️ Morning Check-in", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("What time did you start your day?", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("6:00", "7:00", "8:00", "9:00").forEach { time ->
                                Button(
                                    onClick = { 
                                        val parts = time.split(":")
                                        val mockWake = java.time.LocalDate.now().atTime(parts[0].toInt(), 0)
                                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                        viewModel.setManualWakeTime(mockWake)
                                    }
                                ) { Text(time) }
                            }
                        }
                    }
                }
            }

            // 4. Human Battery Section
            Text("Daily Charge", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth(0.75f)) {
                BodyBatteryGauge(
                    score = state.energyScore?.score ?: 75,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(Modifier.height(32.dp))

            // 5. Work Strain & OT Focus (Improved)
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WorkHistory, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Work Strain Focus", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Sitting for 90+ mins detected. Flush adenosine with a 2-min zone 2 movement.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 6. Strain vs Recovery (Visual Value)
            Text("Metabolic Balance", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            PieChartView(
                entries = listOf(
                    "Recovery (Sleep)" to (state.energyScore?.sleepScore?.toFloat() ?: 60f),
                    "Strain (Activity)" to (state.energyScore?.activityBalanceScore?.toFloat() ?: 40f)
                ),
                onSliceClick = {}
            )

            // 7. Daily Protocol
            state.wakeTime?.let { wake ->
                val fuelingStart = wake.plus(java.time.Duration.ofHours(2))
                val caffeineCutoff = wake.plus(java.time.Duration.ofHours(10))
                val windDown = wake.plus(java.time.Duration.ofHours(14))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Bio-Schedule", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                "Edit", 
                                color = MaterialTheme.colorScheme.primary, 
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.clickable { viewModel.setManualWakeTime(0L) }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        ScheduleRow("☀️ Sunlight Anchor", timeFormatter.format(wake), EnergyYellow)
                        ScheduleRow("🍳 Fueling Start", timeFormatter.format(fuelingStart), EnergyGreen)
                        ScheduleRow("☕ Last Caffeine", timeFormatter.format(caffeineCutoff), EnergyRed)
                        ScheduleRow("🌙 Recovery Wind-down", timeFormatter.format(windDown), EnergyBlue)
                    }
                }
            }

            // 8. Metabolic Guides
            Text(
                text = "Health Intelligence",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                fontWeight = FontWeight.Bold
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GuideCard(
                    title = "Light Protocol",
                    icon = Icons.Default.Lightbulb,
                    modifier = Modifier.weight(1f),
                    color = EnergyYellow,
                    onClick = onNavigateToLightProtocol
                )
                GuideCard(
                    title = "Fueling Rule",
                    icon = Icons.Default.WaterDrop,
                    modifier = Modifier.weight(1f),
                    color = EnergyBlue,
                    onClick = onNavigateToHydration
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            Button(
                onClick = { viewModel.forceSync() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (state.isLoading) "Optimizing Protocol..." else "Recalculate Energy")
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, color: Color, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("Action Plan", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun ScheduleRow(label: String, time: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
        Text(time, color = color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
