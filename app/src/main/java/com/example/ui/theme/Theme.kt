package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = Color(0xFF2ECC71),
    background = DarkBackground,
    surface = DarkBackground,
    onPrimary = Color.White,
    onBackground = NightTextColor,
    onSurface = NightTextColor,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = Color(0xFF27AE60),
    background = LightBackground,
    surface = LightBackground,
    onPrimary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor,
    surfaceVariant = Color(0xFFD1D9E6)
)

@Composable
fun BibLaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
