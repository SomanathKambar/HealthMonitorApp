package com.vkm.healthmonitor.core.designsystem.components

/**
 * Reusable Pie chart component.
 * @param entries list of Pair<label, value>
 */
import android.content.Context
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
    onSliceClick:(String) -> Unit
) {
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
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    val slice = e.label
                    onSliceClick(slice)
//                    val detail = vitalsSummary[slice]
//                    sheetTitle = "${status.name} → $slice Vitals"
//                    sheetDetails = detail?.issues ?: "No details"
//                    sheetRec = detail?.recommendation ?: "No recommendation"
//                    sheetVisible = true
                }
            }

            override fun onNothingSelected() {

            }
        }
        )
    }, modifier = modifier.fillMaxWidth()
        .height(250.dp)
        .padding(bottom = 20.dp))
}
