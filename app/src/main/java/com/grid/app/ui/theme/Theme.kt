package com.grid.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GridColorScheme = darkColorScheme(
    primary = GridCyan,
    onPrimary = Color.Black,
    secondary = GridBlue,
    background = GridBlack,
    surface = GridBlackPanel,
    onBackground = GridWhite,
    onSurface = GridWhite,
    error = GridUrgent
)

@Composable
fun GridTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GridColorScheme,
        typography = GridTypography,
        content = content
    )
}
