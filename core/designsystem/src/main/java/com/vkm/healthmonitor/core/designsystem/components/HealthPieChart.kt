package com.vkm.healthmonitor.core.designsystem.components

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.vkm.healthmonitor.core.model.HealthStatus
import com.vkm.healthmonitor.core.model.ProfileWithVitals

import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun HealthPieChart(profile: ProfileWithVitals, status: HealthStatus, modifier: Modifier = Modifier) {
    var sheetVisible by remember { mutableStateOf(false) }
    var sheetTitle by remember { mutableStateOf("") }
    var sheetDetails by remember { mutableStateOf("") }
    var sheetRec by remember { mutableStateOf("") }

    val vitalsSummary = buildVitalsSummary(profile)
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    Box(modifier) {
        AndroidView(factory = { ctx: Context ->
            PieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setEntryLabelColor(textColor)
                legend.isWordWrapEnabled = true
                legend.textColor = textColor
            }
        }, update = { chart ->
            val entries = listOf(
                PieEntry(status.normalCount.toFloat(), "Normal"),
                PieEntry(status.warningCount.toFloat(), "Warning"),
                PieEntry(status.criticalCount.toFloat(), "Critical")
            )
            val ds = PieDataSet(entries, status.name).apply {
                colors = listOf(
                    android.graphics.Color.parseColor("#4CAF50"), // green
                    android.graphics.Color.parseColor("#FFC107"), // yellow
                    android.graphics.Color.parseColor("#F44336")  // red
                )
                valueTextSize = 12f
                valueTextColor = textColor
            }
            chart.data = PieData(ds)
            chart.setEntryLabelColor(textColor)
            chart.legend.textColor = textColor

            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is PieEntry) {
                        val slice = e.label.uppercase()
                        val detail = vitalsSummary[slice]
                        sheetTitle = "${status.name} → $slice Vitals"
                        sheetDetails = detail?.issues ?: "No details"
                        sheetRec = detail?.recommendation ?: "No recommendation"
                        sheetVisible = true
                    }
                }
                override fun onNothingSelected() {}
            })

            chart.invalidate()
        }, modifier = Modifier
            .fillMaxWidth()
            .height(250.dp))

        androidx.compose.material3.Text(
            "${status.name}: ${status.normalCount} normal, ${status.warningCount} warning, ${status.criticalCount} critical vitals.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    VitalsInfoBottomSheet(
        visible = sheetVisible,
        onDismiss = { sheetVisible = false },
        title = sheetTitle,
        details = sheetDetails,
        recommendation = sheetRec
    )
}