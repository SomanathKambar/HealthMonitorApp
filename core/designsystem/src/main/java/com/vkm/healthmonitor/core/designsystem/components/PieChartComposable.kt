package com.vkm.healthmonitor.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.vkm.healthmonitor.core.model.FamilySliceSummary

@Composable
fun PieChartComposable(
    familySummary: List<FamilySliceSummary>,
    modifier: Modifier = Modifier,
    onSliceClick: (String) -> Unit
) {
    val context = LocalContext.current

    AndroidView(factory = { ctx ->
        PieChart(ctx).apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setEntryLabelColor(android.graphics.Color.BLACK)
            legend.isWordWrapEnabled = true
        }
    }, update = { chart ->
        // Build entries
        val entries = familySummary.map { fs ->
            PieEntry(fs.count.toFloat(), fs.slice.name)
        }
        val ds = PieDataSet(entries, "Family Health").apply {
            colors = listOf(
                android.graphics.Color.parseColor("#4CAF50"),
                android.graphics.Color.parseColor("#FFC107"),
                android.graphics.Color.parseColor("#F44336")
            )
            valueTextSize = 12f
        }
        chart.data = PieData(ds)
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: com.github.mikephil.charting.data.Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    onSliceClick(e.label)
                }
            }
            override fun onNothingSelected() { }
        })
        chart.invalidate()
    }, modifier = modifier)
}