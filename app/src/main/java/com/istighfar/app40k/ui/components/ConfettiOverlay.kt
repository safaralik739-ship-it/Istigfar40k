package com.istighfar.app40k.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private data class ConfettiPiece(
    val startXFraction: Float,
    val color: Color,
    val delayMs: Int,
    val rotationSpeed: Float
)

/**
 * Полноэкранный оверлей с конфетти и поздравлением при достижении цели.
 */
@Composable
fun ConfettiOverlay(
    milestone: Int,
    onDismiss: () -> Unit
) {
    val confettiColors = listOf(
        Color(0xFFD4AF37), Color(0xFF2E9A63), Color(0xFFFFFFFF),
        Color(0xFF1E7A4C), Color(0xFFE8CE7A)
    )
    val pieces = remember {
        List(60) {
            ConfettiPiece(
                startXFraction = Random.nextFloat(),
                color = confettiColors.random(),
                delayMs = Random.nextInt(0, 400),
                rotationSpeed = Random.nextFloat() * 6f + 2f
            )
        }
    }

    var animationProgress by remember { mutableStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 2200, easing = LinearEasing),
        label = "confetti"
    )

    LaunchedEffect(Unit) { animationProgress = 1f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            pieces.forEach { piece ->
                val fallProgress = (progress).coerceIn(0f, 1f)
                val y = size.height * fallProgress
                val x = size.width * piece.startXFraction
                rotate(degrees = fallProgress * 360f * piece.rotationSpeed, pivot = androidx.compose.ui.geometry.Offset(x, y)) {
                    drawRect(
                        color = piece.color,
                        topLeft = androidx.compose.ui.geometry.Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(10f, 16f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(32.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(28.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉", fontSize = 48.sp)
            Text(
                "Машаллах!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Вы достигли $milestone произнесений истигфара!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
            Button(
                onClick = onDismiss,
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Продолжить")
            }
        }
    }
}
