package com.vkm.healthmonitor.core.designsystem.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.vkm.healthmonitor.core.designsystem.theme.EnergyBlue
import com.vkm.healthmonitor.core.designsystem.theme.EnergyGreen
import com.vkm.healthmonitor.core.designsystem.theme.EnergyRed
import com.vkm.healthmonitor.core.designsystem.theme.EnergyYellow

@Composable
fun AnimatedBatteryIcon(
    level: Int, // 0-100
    modifier: Modifier = Modifier.size(40.dp, 20.dp)
) {
    val animatedLevel by animateFloatAsState(
        targetValue = level.toFloat() / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "batteryLevel"
    )

    val color = when {
        level >= 70 -> EnergyGreen
        level >= 30 -> EnergyYellow
        else -> EnergyRed
    }

    // Low battery pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (level < 20) 0.4f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaPulse"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 2.dp.toPx()
        val width = size.width
        val height = size.height
        val tipWidth = width * 0.1f
        val bodyWidth = width - tipWidth

        // Battery Body
        drawRoundRect(
            color = Color.Gray.copy(alpha = 0.5f),
            size = Size(bodyWidth, height),
            cornerRadius = CornerRadius(4.dp.toPx()),
            style = Stroke(width = strokeWidth)
        )

        // Battery level fill
        drawRoundRect(
            color = color.copy(alpha = alpha),
            topLeft = Offset(strokeWidth * 1.5f, strokeWidth * 1.5f),
            size = Size((bodyWidth - strokeWidth * 3f) * animatedLevel, height - strokeWidth * 3f),
            cornerRadius = CornerRadius(2.dp.toPx())
        )

        // Battery Tip
        drawRoundRect(
            color = Color.Gray.copy(alpha = 0.5f),
            topLeft = Offset(bodyWidth, height * 0.3f),
            size = Size(tipWidth, height * 0.4f),
            cornerRadius = CornerRadius(2.dp.toPx())
        )
    }
}
