package com.vkm.healthmonitor.core.designsystem.components

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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//@Composable
//fun LineChartView(label: String, entries: List<Entry>, color: Color, modifier: Modifier = Modifier) {
//    if (entries.isEmpty()) return
//
//    AndroidView(factory = { ctx: Context ->
//        LineChart(ctx).apply {
//            description = Description().apply { text = label }
//            axisRight.isEnabled = false
//            legend.isEnabled = true
//        }
//    }, update = { chart ->
//        val ds = LineDataSet(entries, label).apply {
//            setDrawCircles(false)
//            lineWidth = 2f
//            this.color = color.toArgb()
//            valueTextColor = color.toArgb()
//        }
//        chart.data = LineData(ds)
//        chart.invalidate()
//    }, modifier = modifier.fillMaxWidth().height(250.dp))
//}

//@Composable
//fun LineChartView(
//    title: String,
//    entries: List<Entry>,
//    color: Color,
//    modifier: Modifier = Modifier
//        .fillMaxWidth()
//        .height(200.dp)
//) {
//    Column (modifier = Modifier.padding(8.dp)) {
//        Text(title, style = MaterialTheme.typography.body2)
//
//        if (entries.isEmpty()) {
//            Text("No data available")
//            return
//        }
//
//        Canvas (modifier = modifier) {
//            val maxY = (entries.maxOf { it.y } * 1.2f).coerceAtLeast(1f)
//            val minY = (entries.minOf { it.y } * 0.8f)
//            val maxX = entries.size - 1
//
//            val xStep = size.width / maxX.coerceAtLeast(1)
//            val yScale = size.height / (maxY - minY)
//
//            val path = Path()
//            entries.forEachIndexed { index, entry ->
//                val x = index * xStep
//                val y = size.height - (entry.y - minY) * yScale
//
//                if (index == 0) {
//                    path.moveTo(x, y)
//                } else {
//                    path.lineTo(x, y)
//                }
//
//                // Draw point marker (circle)
//                drawCircle(
//                    color = color,
//                    radius = 6f,
//                    center = Offset(x, y)
//                )
//            }
//
//            // Draw line
//            drawPath(
//                path = path,
//                color = color,
//                style = Stroke(width = 4f)
//            )
//        }
//    }
//}
//

//@Composable
//fun LineChartView(
//    title: String,
//    entries: List<Entry>,
//    color: Color,
//    modifier: Modifier = Modifier
//        .fillMaxWidth()
//        .height(200.dp)
//) {
//    Column(modifier = Modifier.padding(8.dp)) {
//        Text(title, style = MaterialTheme.typography.body1)
//
//        if (entries.isEmpty()) {
//            Text("No data available")
//            return
//        }
//
//        Canvas(modifier = modifier) {
//            val maxY = (entries.maxOf { it.y } * 1.2f).coerceAtLeast(1f)
//            val minY = (entries.minOf { it.y } * 0.8f)
//            val maxX = entries.maxOf { it.x }
//
//            val xStep = size.width / maxX.coerceAtLeast(1f)
//            val yScale = size.height / (maxY - minY)
//
//            val path = Path()
//            entries.forEachIndexed { index, entry ->
//                val x = entry.x * xStep
//                val y = size.height - (entry.y - minY) * yScale
//
//                if (index == 0) {
//                    path.moveTo(x, y)
//                } else {
//                    path.lineTo(x, y)
//                }
//
//                // Draw point marker
//                drawCircle(
//                    color = color,
//                    radius = 6f,
//                    center = Offset(x, y)
//                )
//            }
//
//            // Draw line connecting points
//            drawPath(
//                path = path,
//                color = color,
//                style = Stroke(width = 3f)
//            )
//        }
//    }
//}

@Composable
fun LineChartView(
    label: String,
    entries: List<Entry>,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    AndroidView(factory = { ctx: Context ->
        LineChart(ctx).apply {
            description = Description().apply { text = label }
            axisRight.isEnabled = false
            legend.isEnabled = true

            // X axis as time (timestamp)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    // format timestamp -> HH:mm
                    val date = Date(value.toLong())
                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                    return format.format(date)
                }
            }
        }
    }, update = { chart ->
        val ds = LineDataSet(entries, label).apply {
            setDrawCircles(true)        // show dots
            circleRadius = 4f           // marker size
            setCircleColor(color.toArgb())
            lineWidth = 2f
            this.color = color.toArgb()
            valueTextColor = color.toArgb()
            setDrawValues(false)        // hide value text over points
            mode = LineDataSet.Mode.LINEAR // smooth ECG-style
        }
        chart.data = LineData(ds)
        chart.invalidate()
    }, modifier = modifier.fillMaxWidth().height(250.dp))
}
