package com.example.cars24clone.sdui.runtime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.cars24clone.sdui.model.SduiStyle
import com.example.cars24clone.ui.theme.CarsBadge
import com.example.cars24clone.ui.theme.CarsBuyCard
import com.example.cars24clone.ui.theme.CarsCream
import com.example.cars24clone.ui.theme.CarsHero
import com.example.cars24clone.ui.theme.CarsOrbit
import com.example.cars24clone.ui.theme.CarsSellCard

fun spaceToken(token: String?): Dp = when (token) {
    "space.xs" -> 4.dp
    "space.sm" -> 8.dp
    "space.md" -> 16.dp
    "space.lg" -> 24.dp
    "space.xl" -> 32.dp
    else -> 0.dp
}

fun radiusToken(token: String?): Dp = when (token) {
    "radius.sm" -> 8.dp
    "radius.md" -> 12.dp
    "radius.lg" -> 20.dp
    else -> 0.dp
}

@Composable
fun colorToken(token: String?): Color {
    val colors = MaterialTheme.colorScheme
    return when (token) {
        "color.bg" -> colors.background
        "color.surface" -> colors.surface
        "color.primary" -> colors.primary
        "color.hero" -> CarsHero
        "color.onPrimary", "color.onHero" -> Color.White
        "color.text" -> colors.onBackground
        "color.muted" -> colors.onSurfaceVariant
        "color.border" -> colors.outline
        "color.orbit" -> CarsOrbit
        "color.buyCard" -> CarsBuyCard
        "color.sellCard" -> CarsSellCard
        "color.cream" -> CarsCream
        "color.badge" -> CarsBadge
        else -> Color.Unspecified
    }
}

@Composable
fun SduiStyle.toModifier(): Modifier {
    var modifier: Modifier = Modifier
    val shape = if (corner != null) RoundedCornerShape(radiusToken(corner)) else null
    val background = colorToken(background)
    if (background != Color.Unspecified) {
        modifier = if (shape != null) {
            modifier.clip(shape).background(background)
        } else {
            modifier.background(background)
        }
    } else if (shape != null) {
        modifier = modifier.clip(shape)
    }
    if (insets == "statusBars") {
        modifier = modifier.statusBarsPadding()
    }
    padding?.let { modifier = modifier.padding(spaceToken(it)) }
    if (paddingH != null || paddingV != null) {
        modifier = modifier.padding(
            horizontal = spaceToken(paddingH),
            vertical = spaceToken(paddingV),
        )
    }
    when (width) {
        "fill" -> modifier = modifier.fillMaxWidth()
    }
    height?.toIntOrNull()?.let { modifier = modifier.height(it.dp) }
    width?.toIntOrNull()?.let { modifier = modifier.width(it.dp) }
    return modifier
}
