package com.istighfar.app40k.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Красивое круговое кольцо прогресса с золотым градиентом.
 * percent — значение от 0 до 100.
 */
@Composable
fun ProgressRing(
    percent: Int,
    modifier: Modifier = Modifier,
    strokeWidthDp: Float = 18f,
    trackColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable () -> Unit
) {
    val animatedPercent by animateFloatAsState(
        targetValue = percent.coerceIn(0, 100).toFloat(),
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)) {
            val strokeWidth = strokeWidthDp
            val diameter = size.minDimension
            val topLeftOffset = androidx.compose.ui.geometry.Offset(
                (size.width - diameter) / 2f + strokeWidth / 2f,
                (size.height - diameter) / 2f + strokeWidth / 2f
            )
            val arcSize = androidx.compose.ui.geometry.Size(
                diameter - strokeWidth,
                diameter - strokeWidth
            )

            // Фоновый трек
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Прогресс с золотым градиентом
            val sweep = 360f * (animatedPercent / 100f)
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF2E9A63),
                        androidx.compose.ui.graphics.Color(0xFFD4AF37),
                        androidx.compose.ui.graphics.Color(0xFF2E9A63)
                    )
                ),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        content()
    }
}
