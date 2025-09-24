package com.vkm.healthmonitor.compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mikephil.charting.data.Entry
import com.vkm.healthmonitor.compose.data.model.ProfileWithVitals

/**
 * Wrapper component that shows Family Pie chart and Vitals line chart.
 * Caller must supply profile list and selected profile vitals list.
 */
@Composable
fun ChartsForProfiles(profilesWithVitals: List<ProfileWithVitals>) {
    LazyColumn(
        Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed( profilesWithVitals) { index, pwv ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${pwv.profile.name} (${pwv.profile.relationTo ?: "Self"})", modifier = Modifier.padding(bottom = 8.dp))
                    val vitals = pwv.vitals.sortedBy { it.timestamp }
                    if (vitals.isEmpty()) {
                        Text("No vitals recorded.")
                    } else {
                        // ensure at least two points per chart for line drawing
                        fun <T> ensureTwo(list: List<T>): List<T> = if (list.size == 1) list + list else list
                        LineChartView("Pulse", ensureTwo(vitals.map { Entry(it.timestamp.toFloat(), it.pulse.toFloat()) }), Color.Red)
                        LineChartView("Systolic", ensureTwo(vitals.map { Entry(it.timestamp.toFloat(), it.bpSys.toFloat()) }), Color.Blue)
                        LineChartView("Diastolic", ensureTwo(vitals.map { Entry(it.timestamp.toFloat(), it.bpDia.toFloat()) }), Color.Green)
                        LineChartView("Temp", ensureTwo(vitals.map { Entry(it.timestamp.toFloat(), it.temperature) }), Color.Magenta)
                        LineChartView("SpO₂", ensureTwo(vitals.map { Entry(it.timestamp.toFloat(), it.spo2.toFloat()) }), Color.Cyan)
                    }
                }
            }
        }

        // family pie chart at the bottom
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Family Health Overview")
                    val entries = profilesWithVitals.map { it.profile.name to it.profile.bmi }
                    PieChartView(entries, modifier = Modifier.fillMaxWidth().height(300.dp))
                }
            }
        }
    }
}
