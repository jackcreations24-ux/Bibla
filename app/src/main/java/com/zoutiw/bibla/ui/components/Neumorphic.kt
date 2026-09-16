package com.zoutiw.bibla.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zoutiw.bibla.ui.theme.DarkShadow
import com.zoutiw.bibla.ui.theme.LightShadow
import com.zoutiw.bibla.ui.theme.NightDarkShadow
import com.zoutiw.bibla.ui.theme.NightLightShadow

@Composable
fun NeumorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    isDarkTheme: Boolean = false,
    isPressed: Boolean = false,
    content: @Composable () -> Unit
) {
    val lightShadowColor = if (isDarkTheme) NightLightShadow else LightShadow
    val darkShadowColor = if (isDarkTheme) NightDarkShadow else DarkShadow

    Box(
        modifier = modifier
            .then(
                if (isPressed) {
                    Modifier.neumorphicPressedShadow(
                        cornerRadius = cornerRadius,
                        elevation = elevation,
                        lightShadowColor = lightShadowColor,
                        darkShadowColor = darkShadowColor
                    )
                } else {
                    Modifier.neumorphicShadow(
                        cornerRadius = cornerRadius,
                        elevation = elevation,
                        lightShadowColor = lightShadowColor,
                        darkShadowColor = darkShadowColor
                    )
                }
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        content()
    }
}

fun Modifier.neumorphicShadow(
    cornerRadius: Dp,
    elevation: Dp,
    lightShadowColor: Color,
    darkShadowColor: Color
): Modifier = this.composed {
    val paint = remember { Paint() }
    val frameworkPaint = remember { paint.asFrameworkPaint() }
    
    this.drawBehind {
        val shadowBlur = elevation.toPx() * 1.5f // Increased blur for "soft" look
        val offsetX = elevation.toPx() / 2
        val offsetY = elevation.toPx() / 2

        drawIntoCanvas { canvas ->
            // Dark Shadow
            frameworkPaint.color = darkShadowColor.toArgb()
            frameworkPaint.setShadowLayer(shadowBlur, offsetX, offsetY, darkShadowColor.toArgb())
            canvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                paint
            )

            // Light Shadow
            frameworkPaint.color = lightShadowColor.toArgb()
            frameworkPaint.setShadowLayer(shadowBlur, -offsetX, -offsetY, lightShadowColor.toArgb())
            canvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                paint
            )
        }
    }
}

fun Modifier.neumorphicPressedShadow(
    cornerRadius: Dp,
    elevation: Dp,
    lightShadowColor: Color,
    darkShadowColor: Color
): Modifier = this.composed {
    val paint = remember { Paint() }
    val frameworkPaint = remember { paint.asFrameworkPaint() }
    
    this.drawBehind {
        val shadowBlur = elevation.toPx() // Subtle blur for pressed state
        val offsetX = elevation.toPx() / 4
        val offsetY = elevation.toPx() / 4

        drawIntoCanvas { canvas ->
            // Simulating pressed effect with slightly more intense inset shadows
            paint.color = darkShadowColor.copy(alpha = 0.3f)
            frameworkPaint.setShadowLayer(shadowBlur, offsetX, offsetY, darkShadowColor.toArgb())
            canvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                paint
            )

            paint.color = lightShadowColor.copy(alpha = 0.3f)
            frameworkPaint.setShadowLayer(shadowBlur, -offsetX, -offsetY, lightShadowColor.toArgb())
            canvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(),
                paint
            )
        }
    }
}

@Composable
fun NeumorphicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    NeumorphicCard(
        modifier = modifier.clickable { onClick() },
        cornerRadius = cornerRadius,
        elevation = elevation,
        backgroundColor = backgroundColor,
        isDarkTheme = isDarkTheme
    ) {
        Box(
            modifier = Modifier.padding(12.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
fun NeumorphicIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    cornerRadius: Dp = 10.dp,
    elevation: Dp = 2.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    isDarkTheme: Boolean = false
) {
    NeumorphicCard(
        modifier = modifier.size(size),
        cornerRadius = cornerRadius,
        elevation = elevation,
        isDarkTheme = isDarkTheme
    ) {
        Box(contentAlignment = androidx.compose.ui.Alignment.Center, modifier = Modifier.fillMaxSize()) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(size * 0.5f),
                tint = tint
            )
        }
    }
}

