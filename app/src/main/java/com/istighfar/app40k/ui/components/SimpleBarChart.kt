package com.istighfar.app40k.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Простая столбчатая диаграмма недельной активности (без внешних библиотек графиков).
 */
@Composable
fun SimpleBarChart(
    values: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF2E9A63),
    accentColor: Color = Color(0xFFD4AF37)
) {
    val maxValue = (values.maxOrNull() ?: 0).coerceAtLeast(1)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 4.dp)
        ) {
            val barCount = values.size.coerceAtLeast(1)
            val spacing = size.width * 0.02f
            val barWidth = (size.width - spacing * (barCount + 1)) / barCount

            values.forEachIndexed { index, value ->
                val barHeight = (value.toFloat() / maxValue) * size.height * 0.9f
                val left = spacing + index * (barWidth + spacing)
                val top = size.height - barHeight
                val color = if (index == values.lastIndex) accentColor else barColor
                drawRoundRect(
                    color = color,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                )
            }
        }
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
