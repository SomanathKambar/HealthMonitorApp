package com.vkm.healthmonitor.compose.ui.components

import com.vkm.healthmonitor.compose.data.model.Profile
import com.vkm.healthmonitor.compose.data.model.VitalEntry

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import androidx.compose.ui.Modifier
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun FamilyHealthCard(
    profiles: List<Profile>,
    vitalsAverages: Map<Int, Float>, // profileId -> healthScore (0–100)
    modifier: Modifier = Modifier
) {
    val entries = profiles.map { p ->
        val score = vitalsAverages[p.id] ?: 50f
        PieEntry(score, p.name)
    }

    AndroidView(factory = { ctx ->
        PieChart(ctx).apply {
            val ds = PieDataSet(entries, "Family Health")
            ds.colors = ColorTemplate.MATERIAL_COLORS.toList()
            data = PieData(ds)
            description.isEnabled = false
            invalidate()
        }
    }, modifier = modifier)
}

