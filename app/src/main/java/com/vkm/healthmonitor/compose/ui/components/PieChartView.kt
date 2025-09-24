package com.vkm.healthmonitor.compose.ui.components

/**
 * Reusable Pie chart component.
 * @param entries list of Pair<label, value>
 */
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun PieChartView(entries: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    if (entries.isEmpty()) return

    AndroidView(factory = { ctx: Context ->
        PieChart(ctx).apply {
            description.isEnabled = false
            setUsePercentValues(false)
            setEntryLabelColor(android.graphics.Color.BLACK)
            legend.isWordWrapEnabled = true
        }
    }, update = { chart ->
        val pe = entries.map { PieEntry(it.second, it.first) }
        val ds = PieDataSet(pe, "").apply {
            setColors(*ColorTemplate.MATERIAL_COLORS)
            valueTextSize = 12f
        }
        chart.data = PieData(ds)
        chart.invalidate()
    }, modifier = modifier.fillMaxWidth().height(250.dp))
}
