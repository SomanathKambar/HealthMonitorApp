package com.vkm.healthmonitor.feature.home

import android.app.TimePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.health.connect.client.PermissionController
import com.vkm.healthmonitor.core.designsystem.components.AnimatedBatteryIcon
import com.vkm.healthmonitor.core.designsystem.components.PieChartView
import com.vkm.healthmonitor.core.designsystem.theme.*
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

import com.vkm.healthmonitor.core.designsystem.components.VitalsInfoBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnergyDashboardScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHydration: () -> Unit = {},
    onNavigateToLightProtocol: () -> Unit = {},
    onNavigateToVitalHistory: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    val hcPermissions = viewModel.permissions

    var sheetVisible by remember { mutableStateOf(false) }
    var sheetTitle by remember { mutableStateOf("") }
    var sheetDetails by remember { mutableStateOf("") }
    var sheetRec by remember { mutableStateOf("") }

    val currentTip = remember(System.currentTimeMillis() / 60000) {
        val now = LocalTime.now()
        when {
            now.hour in 5..7 -> "☀️ Rise and Grind: Anchor your circadian rhythm with 10 mins of sunlight. Skip the email, find the sun."
            now.hour in 8..9 -> "☕ Fueling Window: Protein-heavy breakfast detected. Your brain needs amino acids, not just caffeine."
            now.hour in 10..11 -> "💻 Deep Work: 90-min focus block. If you've been sitting, stand up for 2 mins. Flush that adenosine!"
            now.hour in 12..13 -> "🥗 Metabolic Reset: Lunch is a tactical decision. Low glycemic load = no afternoon slump. Choose wisely."
            now.hour in 14..15 -> "🔋 The Dip: Energy is flagging? Try a 10-min NSDR or a brisk walk. Another coffee is a debt you'll pay tonight."
            now.hour in 16..17 -> "📊 Home Stretch: Final sprint. Hydrate now to keep cognitive performance peak until logout."
            now.hour in 18..20 -> "🌙 Wind Down: Laptop closed. Blue light is the enemy now. Transition to 'Recovery Mode'."
            else -> "💤 Deep Recovery: Your body is repairing the corporate damage. Sleep is the ultimate performance enhancer."
        }
    }

    val showTimePicker = {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val mockWake = java.time.LocalDate.now().atTime(hour, minute)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                viewModel.setManualWakeTime(mockWake)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

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
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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

            // Witty Corporate Tip - Animated
            AnimatedContent(
                targetState = currentTip,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tipAnimation"
            ) { tip ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(tip, style = MaterialTheme.typography.bodySmall, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
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
                            listOf("7:00", "8:00").forEach { time ->
                                Button(
                                    onClick = { 
                                        val parts = time.split(":")
                                        val mockWake = java.time.LocalDate.now().atTime(parts[0].toInt(), 0)
                                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                        viewModel.setManualWakeTime(mockWake)
                                    }
                                ) { Text(time) }
                            }
                            Button(onClick = { showTimePicker() }) {
                                Text("Pick Time")
                            }
                        }
                    }
                }
            }

            // 4. Human Battery Section
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Bio-Battery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "View History", 
                    color = MaterialTheme.colorScheme.primary, 
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.clickable { onNavigateToVitalHistory() }
                )
            }
            Spacer(Modifier.height(16.dp))
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AnimatedBatteryIcon(
                        level = state.dynamicBattery,
                        modifier = Modifier.size(120.dp, 60.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${state.dynamicBattery}%",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            state.dynamicBattery >= 70 -> EnergyGreen
                            state.dynamicBattery >= 30 -> EnergyYellow
                            else -> EnergyRed
                        }
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))

            // Manual Adjustments
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EnergyAdjustmentButton(
                    label = "Quick Rest",
                    icon = Icons.Default.Hotel,
                    color = EnergyBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.adjustEnergy("REST", 15, "15-min power nap") }
                )
                EnergyAdjustmentButton(
                    label = "Fuel Intake",
                    icon = Icons.Default.Restaurant,
                    color = EnergyGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.adjustEnergy("FOOD", 10, "Balanced meal logged") }
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EnergyAdjustmentButton(
                    label = "Deep Work",
                    icon = Icons.Default.WorkHistory,
                    color = EnergyRed,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.adjustEnergy("STRESS", -10, "Intense focus session") }
                )
                EnergyAdjustmentButton(
                    label = "Activity",
                    icon = Icons.Default.DirectionsRun,
                    color = EnergyYellow,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.adjustEnergy("EXERCISE", -15, "Zone 2 movement") }
                )
            }

            Spacer(Modifier.height(32.dp))

            // 6. Strain vs Recovery
            Text("Metabolic Balance", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            
            val total = (state.recoveryValue + state.strainValue).coerceAtLeast(1f)
            
            PieChartView(
                entries = listOf(
                    "Recovery" to (state.recoveryValue / total * 100f),
                    "Strain" to (state.strainValue / total * 100f)
                ),
                onSliceClick = { label ->
                    sheetTitle = if (label == "Recovery") "Recovery Details" else "Strain Details"
                    if (label == "Recovery") {
                        sheetDetails = "Your recovery is driven by last night's sleep (${state.energyScore?.sleepScore ?: 0}%) and your fueling today."
                        sheetRec = if (state.recoveryValue < 50) "Critical: Prioritize high-quality protein and consider an early wind-down tonight." else "Good: Keep maintaining your fueling protocol."
                    } else {
                        sheetDetails = "Strain tracks your physical activity (${state.energyScore?.activityBalanceScore ?: 0}%) and cognitive stress."
                        sheetRec = if (state.strainValue > 70) "Warning: High strain detected. Flush adenosine with zone 2 movement or deep rest." else "Normal: Strain levels are within optimal range for growth."
                    }
                    sheetVisible = true
                }
            )

            VitalsInfoBottomSheet(
                visible = sheetVisible,
                onDismiss = { sheetVisible = false },
                title = sheetTitle,
                details = sheetDetails,
                recommendation = sheetRec
            )

            // 7. Daily Protocol
            state.wakeTime?.let { wake ->
                val fuelingStart = wake.plus(java.time.Duration.ofHours(2))
                val caffeineCutoff = wake.plus(java.time.Duration.ofHours(10))
                val windDown = wake.plus(java.time.Duration.ofHours(14))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Bio-Schedule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(
                                "Adjust", 
                                color = MaterialTheme.colorScheme.primary, 
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.clickable { showTimePicker() }
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

@Composable
fun EnergyAdjustmentButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
            Text("Action Plan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ScheduleRow(label: String, time: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        Text(time, color = color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}