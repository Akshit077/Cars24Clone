package com.example.cars24clone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CarsHero,
    onPrimary = Color.White,
    background = CarsBackground,
    surface = CarsSurface,
    onBackground = CarsText,
    onSurface = CarsText,
    tertiary = CarsOrbit,
    error = CarsBadge,
)

@Composable
fun Cars24CloneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
