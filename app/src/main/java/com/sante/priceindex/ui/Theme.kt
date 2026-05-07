package com.sante.priceindex.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val GreenPrimary    = Color(0xFF1B5E20)
val GreenContainer  = Color(0xFF388E3C)
val AmberAccent     = Color(0xFFF9A825)
val SurfaceLight    = Color(0xFFF1F8E9)
val OnGreen         = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary          = GreenPrimary,
    onPrimary        = OnGreen,
    primaryContainer = Color(0xFFC8E6C9),
    secondary        = AmberAccent,
    onSecondary      = Color(0xFF1A1A1A),
    background       = Color(0xFFF9FBF7),
    surface          = Color(0xFFFFFFFF),
    onBackground     = Color(0xFF1A1A1A),
    onSurface        = Color(0xFF1A1A1A),
    error            = Color(0xFFB71C1C)
)

@Composable
fun SanteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
