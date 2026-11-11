package com.github.ljarka.toad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ForestGreen = Color(0xFF2E5E3A)
private val MossGreen = Color(0xFF9CC5A1)
private val CapBrown = Color(0xFF8B5A2B)
private val CapTan = Color(0xFFE0B98A)

private val LightColors = lightColorScheme(
    primary = ForestGreen,
    primaryContainer = Color(0xFFD6EAD8),
    onPrimaryContainer = Color(0xFF13331B),
    secondary = CapBrown,
)

private val DarkColors = darkColorScheme(
    primary = MossGreen,
    primaryContainer = Color(0xFF21452A),
    onPrimaryContainer = Color(0xFFD6EAD8),
    secondary = CapTan,
)

@Composable
fun ToadTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}
