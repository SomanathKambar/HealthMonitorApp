package com.vkm.healthmonitor.core.designsystem.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vkm.healthmonitor.core.designsystem.theme.EnergyGreen
import com.vkm.healthmonitor.core.designsystem.theme.EnergyRed
import com.vkm.healthmonitor.core.designsystem.theme.EnergyYellow

@Composable
fun BodyBatteryGauge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000), 
        label = "score"
    )

    val color = when {
        score >= 80 -> EnergyGreen
        score >= 50 -> EnergyYellow
        else -> EnergyRed
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val componentSize = size / 1.2f
            val strokeWidth = 40f
            val topLeft = Offset(
                x = (size.width - componentSize.width) / 2f,
                y = (size.height - componentSize.height) / 2f
            )

            // Background Arc
            drawArc(
                color = Color.DarkGray.copy(alpha = 0.3f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = componentSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Value Arc
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * (animatedScore / 100f),
                useCenter = false,
                topLeft = topLeft,
                size = componentSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${score}%",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                ),
                color = color
            )
            Text(
                text = "Body Battery",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }
    }
}
