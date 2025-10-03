package com.vkm.healthmonitor.compose.ui.components


//@Composable
//fun FamilyHealthCard(
//    profiles: List<Profile>,
//    vitalsAverages: Map<Int, Float>, // profileId -> healthScore (0–100)
//    modifier: Modifier = Modifier
//) {
//    val entries = profiles.map { p ->
//        val score = vitalsAverages[p.id] ?: 50f
//        PieEntry(score, p.name)
//    }
//
//    AndroidView(factory = { ctx ->
//        PieChart(ctx).apply {
//            val ds = PieDataSet(entries, "Family Health")
//            ds.colors = ColorTemplate.MATERIAL_COLORS.toList()
//            data = PieData(ds)
//            description.isEnabled = false
//            invalidate()
//        }
//    }, modifier = modifier)
//}

//@Composable
//fun FamilyHealthCard(profiles: List<ProfileWithVitals>) {
//    val familyStatus = profiles.map { calculateHealthStatus(it) }
//
//    Card (Modifier
//        .fillMaxWidth()
//        .padding(12.dp)) {
//        Column (Modifier.padding(12.dp)) {
//            Text("Family Health Overview", style = MaterialTheme.typography.titleMedium)
//
//            profiles.forEach { pf ->
//                HealthPieChart(pf,  calculateHealthStatus(pf), Modifier.padding(vertical = 8.dp))
//            }
//            // One pie per profile
////            familyStatus.forEach { status ->
////                HealthPieChart(. status, Modifier.padding(vertical = 8.dp))
////            }
//
//            // Aggregate family risk
//            val totalNormal = familyStatus.sumOf { it.normalCount }
//            val totalWarning = familyStatus.sumOf { it.warningCount }
//            val totalCritical = familyStatus.sumOf { it.criticalCount }
//            if (profiles.isNotEmpty()) {
//                HealthPieChart(
//                    profiles.first(),
//                    HealthStatus("Whole Family", totalNormal, totalWarning, totalCritical),
//                    Modifier.padding(top = 12.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun FamilyHealthCard(profilesWithVitals: List<ProfileWithVitals>) {
//    val familySummary = computeFamilyHealth(profilesWithVitals)
//    PieChartView(
//        entries = familySummary.map { it.profile.name to it.healthScore },
//        modifier = Modifier.fillMaxWidth().height(250.dp),
//    ) {
//
//    }
//}


//@Composable
//fun HealthPieChart(status: HealthStatus, modifier: Modifier = Modifier) {
//    Column(modifier = modifier) {
//        AndroidView(factory = { ctx: Context ->
//            PieChart(ctx).apply {
//                description.isEnabled = false
//                setUsePercentValues(true)
//                setEntryLabelColor(android.graphics.Color.BLACK)
//                legend.isWordWrapEnabled = true
//            }
//        }, update = { chart ->
//            val entries = listOf(
//                PieEntry(status.normalCount.toFloat(), "Normal"),
//                PieEntry(status.warningCount.toFloat(), "Warning"),
//                PieEntry(status.criticalCount.toFloat(), "Critical")
//            )
//            val ds = PieDataSet(entries, status.name).apply {
//                colors = listOf(
//                    android.graphics.Color.parseColor("#4CAF50"), // green
//                    android.graphics.Color.parseColor("#FFC107"), // yellow
//                    android.graphics.Color.parseColor("#F44336")  // red
//                )
//                valueTextSize = 12f
//            }
//            chart.data = PieData(ds)
//            chart.invalidate()
//        }, modifier = Modifier.fillMaxWidth().height(250.dp))
//
//        // Text summary
//        Text(
//            "${status.name}: ${status.normalCount} normal, ${status.warningCount} warning, ${status.criticalCount} critical vitals.",
//            style = MaterialTheme.typography.bodySmall,
//            modifier = Modifier.padding(top = 8.dp)
//        )
//    }
//}

//@Composable
//fun HealthPieChart(
//    status: HealthStatus,
//    modifier: Modifier = Modifier
//) {
//    val context = LocalContext.current
//    val vitalsSummary = buildVitalsSummary(status)
//
//    AndroidView(factory = { ctx: Context ->
//        PieChart(ctx).apply {
//            description.isEnabled = false
//            setUsePercentValues(true)
//            setEntryLabelColor(android.graphics.Color.BLACK)
//            legend.isWordWrapEnabled = true
//        }
//    }, update = { chart ->
//        val entries = listOf(
//            PieEntry(status.normalCount.toFloat(), "Normal"),
//            PieEntry(status.warningCount.toFloat(), "Warning"),
//            PieEntry(status.criticalCount.toFloat(), "Critical")
//        )
//        val ds = PieDataSet(entries, status.name).apply {
//            colors = listOf(
//                android.graphics.Color.parseColor("#4CAF50"), // green
//                android.graphics.Color.parseColor("#FFC107"), // yellow
//                android.graphics.Color.parseColor("#F44336")  // red
//            )
//            valueTextSize = 12f
//        }
//        chart.data = PieData(ds)
//
//        // Handle slice clicks
//        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//            override fun onValueSelected(e: Entry?, h: Highlight?) {
//                if (e is PieEntry) {
//                    val slice = e.label
//                    val details = vitalsSummary[slice] ?: "No details"
//                    Toast.makeText(
//                        context,
//                        "${status.name} → $slice issues: $details",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//
//            override fun onNothingSelected() {}
//        })
//
//        chart.invalidate()
//    }, modifier = modifier.fillMaxWidth().height(250.dp))
//
//    // Text summary always shown
//    Text(
//        "${status.name}: ${status.normalCount} normal, ${status.warningCount} warning, ${status.criticalCount} critical vitals.",
//        style = MaterialTheme.typography.bodySmall,
//        modifier = Modifier.padding(top = 8.dp)
//    )
//}
//
///**
// * Returns details of which vitals are contributing to each slice.
// */
//fun buildVitalsSummary(status: HealthStatus): Map<String, String> {
//    val map = mutableMapOf<String, String>()
//
//    // Example mapping (expand with real checks based on profile vitals)
//    map["Normal"] = "All vitals in safe range"
//    map["Warning"] = "Slightly abnormal: e.g., mild BP changes, elevated temp"
//    map["Critical"] = "Severe BP, low SpO₂, or high fever detected"
//
//    return map
//}


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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vkm.healthmonitor.compose.data.model.ProfileIssue
import com.vkm.healthmonitor.compose.data.model.ProfileWithVitals
import com.vkm.healthmonitor.compose.data.model.SliceType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FamilyHealthCard(profilesWithVitals: List<ProfileWithVitals>) {
    val familySummary = remember(profilesWithVitals) {
        computeFamilyHealthSummary(profilesWithVitals)
    }

    // sheet state
    var sheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState (initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope ()
    // content to show in sheet
    var sheetTitle by remember { mutableStateOf("") }
    var sheetContent by remember { mutableStateOf<Pair<String, String>>( "" to "" ) }

    var sheetDetails by remember { mutableStateOf("") }
    var sheetRec by remember { mutableStateOf("") }

    // first = details, second = recommendations

    ModalBottomSheetLayout (
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = sheetTitle, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = sheetContent.first)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = sheetContent.second)
            }
        }
    ) {
        Column {
            PieChartView(
                entries = familySummary.map { it.slice.name to it.count.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                onSliceClick = { sliceName ->
                    // find slice type
                    val sliceType = SliceType.valueOf(sliceName)
                    val summary = familySummary.firstOrNull { it.slice == sliceType }
                    if (summary != null) {
                        if (summary.profiles.isEmpty()) {
                            sheetTitle = sliceName
                            sheetContent = ("All members are OK" to "No action needed, keep healthy habits")
                        } else {
                            // build details
                            sheetTitle = "$sliceName Issues"
                            val details = summary.profiles.joinToString("\n") {
                                "- ${it.profile.name}: ${it.issues}"
                            }
                            val rec = summary.profiles.joinToString("\n") {
                                "- ${it.profile.name}: ${it.recommendation}"
                            }
                            sheetContent = (details to rec)
                        }
                        sheetVisible = true
                        scope.launch { sheetState.show() }
                    }
                }
            )
            // You may show legend / summary texts etc
        }
        FamilyBottomSheet(
            visible = sheetVisible,
            onDismiss = { sheetVisible = false },
            title = sheetTitle,
            sheetContent = sheetContent,
            recommendation = sheetRec
        )
    }
}

fun computeFamilyHealthSummary(profiles: List<ProfileWithVitals>): List<FamilyHealthSummary> {
    // For each profile, compute status per vital, classify per slice
    val profileIssues = profiles.flatMap { pwv ->
        val status = calculateHealthStatus(pwv)  // you have this
        // also compute the issues & recommendation summary
        val vitalsSummary = buildVitalsSummary(pwv)
        listOfNotNull(
            if (status.normalCount > 0)
                ProfileIssue(pwv.profile, SliceType.NORMAL, vitalsSummary["Normal"]?.issues ?: "", vitalsSummary["Normal"]?.recommendation ?: "")
            else null,
            if (status.warningCount > 0)
                ProfileIssue(pwv.profile, SliceType.WARNING, vitalsSummary["Warning"]?.issues ?: "", vitalsSummary["Warning"]?.recommendation ?: "")
            else null,
            if (status.criticalCount > 0)
                ProfileIssue(pwv.profile, SliceType.CRITICAL, vitalsSummary["Critical"]?.issues ?: "", vitalsSummary["Critical"]?.recommendation ?: "")
            else null
        )
    }
    // group by slice
    return SliceType.values().map { slice ->
        val filtered = profileIssues.filter { it.slice == slice }
        FamilyHealthSummary(slice, filtered.size, filtered)
    }
}

data class FamilyHealthSummary(
    val slice: SliceType,
    val count: Int,
    val profiles: List<ProfileIssue>
)

//fun computeFamilyHealth(profilesWithVitals: List<ProfileWithVitals>): List<ProfileHealthSummary> {
//    return profilesWithVitals.map { pwv ->
//        val vitals = pwv.vitals.lastOrNull() // use latest entry for scoring
//        var score = 100f
//        var status = HealthStatusEnum.NORMAL
//
//        if (vitals != null) {
//            // Pulse
//            if (vitals.pulse < 60 || vitals.pulse > 100) {
//                score -= 20
//            }
//            // Systolic BP
//            if (vitals.bpSys < 100 || vitals.bpSys > 129) {
//                score -= 15
//            }
//            // Diastolic BP
//            if (vitals.bpDia < 60 || vitals.bpDia > 85) {
//                score -= 15
//            }
//            // Temperature
//            if (vitals.temperature < 36.5f || vitals.temperature > 37.5f) {
//                score -= 10
//            }
//            // SpO2
//            if (vitals.spo2 < 95) {
//                score -= 20
//            }
//
//            // Determine status
//            status = when {
//                score >= 80 -> HealthStatusEnum.NORMAL
//                score >= 50 -> HealthStatusEnum.WARNING
//                else -> HealthStatusEnum.CRITICAL
//            }
//        }
//
//        ProfileHealthSummary(pwv.profile, score.coerceIn(0f, 100f), status)
//    }
//}

data class HealthStatus(
    val name: String,
    val normalCount: Int,
    val warningCount: Int,
    val criticalCount: Int
)
//
fun calculateHealthStatus(profileWithVitals: ProfileWithVitals): HealthStatus {
    var normal = 0
    var warning = 0
    var critical = 0

    profileWithVitals.vitals.forEach { v ->
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

    return HealthStatus(profileWithVitals.profile.name, normal, warning, critical)
}