package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ultra Pro Max Liquid Crystal Water Glass Effect (iOS 26 Liquid Crystal Morphism).
 * Simulates a translucent, premium crystal glass vessel filled with pure, sparkling living water,
 * with light refractions, aquatic caustics, floating micro-bubbles, water ripples, and
 * pristine chromatic prism glare.
 */
@Composable
fun CrystalWaterGlassBackground(
    spiritualTheme: VerseSpiritualTheme,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crystalWaterLoop")

    // Smooth wave oscillation for surface water meniscus
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    // Second harmonic for complex caustic water reflections
    val causticPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "causticPhase"
    )

    // Specular glass glare sweep across the crystal
    val glareSweep by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glareSweep"
    )

    // Micro-bubble float loop
    val bubbleFloat by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbleFloat"
    )

    // Base colors matching the spiritual theme but enriched with liquid crystal aqua/azure tones
    val liquidBaseColors = if (isDarkTheme) {
        listOf(
            Color(0xFF07192C), // Deep abyssal crystal blue
            spiritualTheme.darkGradient.first().copy(alpha = 0.90f),
            Color(0xFF0A2239),
            spiritualTheme.darkGradient.last().copy(alpha = 0.95f)
        )
    } else {
        listOf(
            Color(0xFFE6F6FF), // Pristine pure ice/water crystal
            spiritualTheme.lightGradient.first().copy(alpha = 0.85f),
            Color(0xFFF0FAFF),
            spiritualTheme.lightGradient.last().copy(alpha = 0.90f)
        )
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. LIQUID BASE GRADIENT (Deep Crystalline Water Volume)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = liquidBaseColors,
                    startY = 0f,
                    endY = h
                )
            )

            // 2. WATER CAUSTICS & REFRACTED SUNLIGHT (Light dancing through water in crystal glass)
            val causticColor1 = if (isDarkTheme) Color(0xFF38BDF8).copy(alpha = 0.12f) else Color(0xFF0284C7).copy(alpha = 0.08f)
            val causticColor2 = if (isDarkTheme) Color(0xFF67E8F9).copy(alpha = 0.09f) else Color(0xFF38BDF8).copy(alpha = 0.07f)

            // Multi-frequency caustic mesh
            val causticPath = Path()
            val step = w / 8f
            for (i in 0..8) {
                val x = i * step
                val y1 = h * 0.30f + sin(causticPhase + i * 0.8f) * 16f
                val y2 = h * 0.65f + cos(causticPhase + i * 1.1f) * 20f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(causticColor1, Color.Transparent),
                        center = Offset(x, y1),
                        radius = w * 0.35f
                    ),
                    radius = w * 0.35f,
                    center = Offset(x, y1)
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(causticColor2, Color.Transparent),
                        center = Offset(w - x, y2),
                        radius = w * 0.30f
                    ),
                    radius = w * 0.30f,
                    center = Offset(w - x, y2)
                )
            }

            // 3. LIVING WATER MENISCUS & TOP LIQUID WAVES (Water surface inside the glass)
            val wavePath1 = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, h * 0.18f)
                val segments = 6
                val segWidth = w / segments
                for (i in 0 until segments) {
                    val x1 = i * segWidth + segWidth / 2f
                    val y1 = h * 0.18f + sin(wavePhase + i * 1.2f) * 9f
                    val x2 = (i + 1) * segWidth
                    val y2 = h * 0.18f + sin(wavePhase + (i + 1) * 1.2f) * 9f
                    quadraticBezierTo(x1, y1, x2, y2)
                }
                lineTo(w, 0f)
                close()
            }
            drawPath(
                path = wavePath1,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        (if (isDarkTheme) Color(0xFF38BDF8) else Color(0xFFBAE6FD)).copy(alpha = if (isDarkTheme) 0.22f else 0.30f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = h * 0.25f
                )
            )

            // 4. SUBMERGED MICRO WATER BUBBLES (Glittering bubbles floating upward)
            val bubblePositions = listOf(
                Pair(0.18f, 0.2f),
                Pair(0.35f, 0.5f),
                Pair(0.52f, 0.8f),
                Pair(0.72f, 0.3f),
                Pair(0.85f, 0.65f),
                Pair(0.28f, 0.9f),
                Pair(0.64f, 0.15f)
            )

            bubblePositions.forEachIndexed { index, (bx, initialOffset) ->
                val currentYFraction = ((bubbleFloat + initialOffset) % 1.0f)
                val bxOscillated = bx * w + sin(wavePhase + index) * 6f
                val byPos = currentYFraction * h
                val bubbleRadius = (3.5f + (index % 3) * 1.8f)

                // Bubble glow
                drawCircle(
                    color = (if (isDarkTheme) Color(0xFF7DD3FC) else Color.White).copy(alpha = 0.40f * (1f - currentYFraction * 0.4f)),
                    radius = bubbleRadius,
                    center = Offset(bxOscillated, byPos)
                )
                // Bubble highlight specular dot
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = bubbleRadius * 0.35f,
                    center = Offset(bxOscillated - bubbleRadius * 0.35f, byPos - bubbleRadius * 0.35f)
                )
            }

            // 5. iOS 26 PRISM SPECULAR GLASS GLARE (Dynamic chromatic glass reflection sweep)
            val glareCenter = Offset(w * glareSweep, h * glareSweep)
            val glareBrush = Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF38BDF8).copy(alpha = 0.05f),
                    Color.White.copy(alpha = if (isDarkTheme) 0.22f else 0.35f),
                    Color(0xFFF472B6).copy(alpha = 0.06f),
                    Color.Transparent
                ),
                start = Offset(glareCenter.x - w * 0.4f, glareCenter.y - h * 0.4f),
                end = Offset(glareCenter.x + w * 0.4f, glareCenter.y + h * 0.4f)
            )
            drawRect(brush = glareBrush)

            // 6. CRYSTAL BEVEL RIM & GLASS REFRACTION CONTOUR (The edge of the glass vessel)
            // Top rim specular reflection
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = if (isDarkTheme) 0.65f else 0.90f),
                        Color(0xFF38BDF8).copy(alpha = 0.80f),
                        Color.White.copy(alpha = if (isDarkTheme) 0.65f else 0.90f),
                        Color.Transparent
                    )
                ),
                start = Offset(w * 0.1f, 1.5f),
                end = Offset(w * 0.9f, 1.5f),
                strokeWidth = 2.5f,
                cap = StrokeCap.Round
            )

            // Inner glass rim reflection (Frosted depth)
            val innerGlassRim = Path().apply {
                moveTo(2f, 2f)
                lineTo(w - 2f, 2f)
                lineTo(w - 2f, h - 2f)
                lineTo(2f, h - 2f)
                close()
            }
            drawPath(
                path = innerGlassRim,
                color = Color.White.copy(alpha = if (isDarkTheme) 0.08f else 0.18f),
                style = Stroke(width = 1.5f)
            )

            // Bottom glass base thickness reflection (vè dlo a gen yon baz epè anba)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        (if (isDarkTheme) Color(0xFF0284C7) else Color(0xFFBAE6FD)).copy(alpha = if (isDarkTheme) 0.20f else 0.25f),
                        (if (isDarkTheme) Color(0xFF38BDF8) else Color.White).copy(alpha = if (isDarkTheme) 0.35f else 0.50f)
                    ),
                    startY = h * 0.88f,
                    endY = h
                ),
                size = Size(w, h * 0.12f),
                topLeft = Offset(0f, h * 0.88f)
            )
        }
    }
}
