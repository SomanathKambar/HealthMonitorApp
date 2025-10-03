package com.vkm.healthmonitor.compose.data.model


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vkm.healthmonitor.compose.ui.components.PieChartComposable
import kotlinx.coroutines.launch

// ---- Your domain/model classes (you likely already have these or similar) ----

data class HealthStatus(
    val name: String,
    val normalCount: Int,
    val warningCount: Int,
    val criticalCount: Int
)

// This holds the content for your bottom sheet

// Suppose this returns a map of slice label → some summary info (issues + rec) for that particular profile


// Helper: build a map, e.g. "Normal" → VitalsSummaryItem(...), etc.
fun buildVitalsSummary(pwv: ProfileWithVitals): Map<String, VitalsSummaryItem> {
    // Your logic: inspect pwv.vitals, decide what is issue / rec for "Normal", "Warning", "Critical"
    // For simplicity, I'll stub:
    return mapOf(
        "Normal" to VitalsSummaryItem("All good", "Keep doing regular checkups"),
        "Warning" to VitalsSummaryItem("Pulse slightly high", "Take rest & monitor daily"),
        "Critical" to VitalsSummaryItem("BP very high, SpO₂ low", "Visit doctor immediately")
    )
}

// Calculate health status counts for one profile
fun calculateHealthStatus2(pwv: ProfileWithVitals): HealthStatus {
    var normal = 0
    var warning = 0
    var critical = 0

    pwv.vitals.forEach { v ->
        // Pulse
        when {
            v.pulse in 60..100 -> normal++
            v.pulse in 50..59 || v.pulse in 101..120 -> warning++
            else -> critical++
        }
        // BP
        when {
            v.bpSys in 100..129 && v.bpDia in 60..85 -> normal++
            v.bpSys in 90..139 || v.bpDia in 55..89 -> warning++
            else -> critical++
        }
        // Temp
        when {
            v.temperature in 36.5..37.5 -> normal++
            v.temperature in 37.6..38.5 -> warning++
            else -> critical++
        }
        // SpO₂
        when {
            v.spo2 >= 95 -> normal++
            v.spo2 in 90..94 -> warning++
            else -> critical++
        }
    }

    return HealthStatus(pwv.profile.name, normal, warning, critical)
}

// Aggregate across the family: slice → summary
data class FamilySliceSummary(
    val slice: SliceType,
    val count: Int,
    val items: List<ProfileSheetItem>
)

data class ProfileSheetItem(
    val profile: Profile,
    val issues: String,
    val recommendation: String
)

fun computeFamilyHealthSummary(
    profilesWithVitals: List<ProfileWithVitals>
): List<FamilySliceSummary> {
    // For each profile, compute its HealthStatus + summary map
    val perProfileSummaries: List<Triple<ProfileWithVitals, HealthStatus, Map<String, VitalsSummaryItem>>> =
        profilesWithVitals.map { pwv ->
            val hs = calculateHealthStatus2(pwv)
            val vsMap = buildVitalsSummary(pwv)
            Triple(pwv, hs, vsMap)
        }

    // For each slice type, collect affected profiles
    return SliceType.values().map { slice ->
        val items = mutableListOf<ProfileSheetItem>()
        perProfileSummaries.forEach { (pwv, hs, vsMap) ->
            val label = slice.name  // e.g. "NORMAL", "WARNING", "CRITICAL"
            val summaryItem = vsMap[label]
            if (summaryItem != null) {
                val countForSlice = when (slice) {
                    SliceType.NORMAL -> hs.normalCount
                    SliceType.WARNING -> hs.warningCount
                    SliceType.CRITICAL -> hs.criticalCount
                }
                if (countForSlice > 0) {
                    items += ProfileSheetItem(
                        profile = pwv.profile,
                        issues = summaryItem.issues,
                        recommendation = summaryItem.recommendation
                    )
                }
            }
        }
        FamilySliceSummary(slice, items.size, items)
    }
}


// ---- The composable with pie + bottom sheet ----

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FamilyHealthCard(profilesWithVitals: List<ProfileWithVitals>) {
    val familySummary = remember(profilesWithVitals) {
        computeFamilyHealthSummary(profilesWithVitals)
    }

    // State for bottom sheet content
    var sheetContent by remember { mutableStateOf<SheetContent?>(null) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            sheetContent?.let { content ->
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = content.issues,
                        style =MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = content.recommendation,
                        style = when (content.severity) {
                            SliceType.CRITICAL -> MaterialTheme.typography.bodySmall.copy(
                                color = Color.Red, fontStyle = FontStyle.Italic
                            )
                            SliceType.WARNING -> MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFB8860B), fontStyle = FontStyle.Italic
                            )
                            SliceType.NORMAL -> MaterialTheme.typography.bodySmall.copy(
                                color = Color.Green
                            )
                        }
                    )
                }
            } ?: Box(modifier = Modifier.height(1.dp)) { /* empty fallback so sheetContent null does not break layout */ }
        },
        sheetGesturesEnabled = true
    ) {
        // Content under the sheet
        Column(modifier = Modifier.fillMaxWidth()) {
            PieChartComposable (
                familySummary = familySummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                onSliceClick = { slice ->
                    val sliceType = SliceType.valueOf(slice)
                    val summary = familySummary.firstOrNull { it.slice == sliceType }
                    if (summary != null) {
                        if (summary.items.isEmpty()) {
                            sheetContent = SheetContent(
                                title = slice,
                                issues = "All members are within normal range.",
                                recommendation = "No action required. Maintain healthy lifestyle.",
                                severity = sliceType
                            )
                        } else {
                            val issuesText = summary.items.joinToString("\n") { item ->
                                "${item.profile.name}: ${item.issues}"
                            }
                            val recText = summary.items.joinToString("\n") { item ->
                                "${item.profile.name}: ${item.recommendation}"
                            }
                            sheetContent = SheetContent(
                                title = "$slice Issues",
                                issues = issuesText,
                                recommendation = recText,
                                severity = sliceType
                            )
                        }
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                }
            )

            // Optionally: some legend or summary text
            Spacer(modifier = Modifier.height(8.dp))
            familySummary.forEach { fs ->
                Text("${fs.slice.name}: ${fs.count}")
            }
        }
    }
}

