package com.vkm.healthmonitor.compose.ui.components

/**
 * Line chart for vitals.
 * - plots pulse, systolic and diastolic on same chart using different datasets
 * - x-axis uses timestamp (millis) and formatted as HH:mm or dd-MM
 */
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun LineChartView(label: String, entries: List<Entry>, color: Color, modifier: Modifier = Modifier) {
    if (entries.isEmpty()) return

    AndroidView(factory = { ctx: Context ->
        LineChart(ctx).apply {
            description = Description().apply { text = label }
            axisRight.isEnabled = false
            legend.isEnabled = true
        }
    }, update = { chart ->
        val ds = LineDataSet(entries, label).apply {
            setDrawCircles(false)
            lineWidth = 2f
            this.color = color.toArgb()
            valueTextColor = color.toArgb()
        }
        chart.data = LineData(ds)
        chart.invalidate()
    }, modifier = modifier.fillMaxWidth().height(250.dp))
}
