package com.vkm.healthmonitor.core.designsystem.components

/**
 * Reusable Pie chart component.
 * @param entries list of Pair<label, value>
 */
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun PieChartView(
    entries: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    onSliceClick: (String) -> Unit
) {
    if (entries.isEmpty()) return
    
    val isDark = isSystemInDarkTheme()
    val labelColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
    val valueColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK

    AndroidView(
        factory = { ctx: Context ->
            PieChart(ctx).apply {
                description.isEnabled = false
                setUsePercentValues(true)
                setEntryLabelColor(labelColor)
                setHoleColor(android.graphics.Color.TRANSPARENT)
                legend.isEnabled = true
                legend.textColor = labelColor
            }
        },
        update = { chart ->
            val pe = entries.map { PieEntry(it.second, it.first) }
            val ds = PieDataSet(pe, "").apply {
                setColors(*ColorTemplate.MATERIAL_COLORS)
                valueTextSize = 12f
                valueTextColor = valueColor
            }
            chart.data = PieData(ds)
            chart.setEntryLabelColor(labelColor)
            chart.legend.textColor = labelColor
            chart.invalidate()
            chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is PieEntry) {
                        onSliceClick(e.label)
                    }
                }
                override fun onNothingSelected() {}
            })
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(bottom = 20.dp)
    )
}
