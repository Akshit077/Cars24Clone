package com.example.cars24clone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.cars24clone.ui.theme.CarsBadge
import com.example.cars24clone.ui.theme.CarsHeroDeep
import com.example.cars24clone.ui.theme.CarsText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

enum class AppTextVariant { Title, Body, Caption, Price, OnHero, OnHeroTitle }

enum class AppButtonVariant { Primary, Secondary, Ghost, Inverse }

enum class AppChipVariant { Filter, Tab }

enum class AppSearchVariant { Default, Hero }

fun appTextVariant(raw: String): AppTextVariant = when (raw) {
    "title" -> AppTextVariant.Title
    "caption" -> AppTextVariant.Caption
    "price" -> AppTextVariant.Price
    "onHero" -> AppTextVariant.OnHero
    "onHeroTitle" -> AppTextVariant.OnHeroTitle
    else -> AppTextVariant.Body
}

fun appButtonVariant(raw: String): AppButtonVariant = when (raw) {
    "secondary" -> AppButtonVariant.Secondary
    "ghost" -> AppButtonVariant.Ghost
    "inverse" -> AppButtonVariant.Inverse
    else -> AppButtonVariant.Primary
}

fun appChipVariant(raw: String): AppChipVariant =
    if (raw == "tab") AppChipVariant.Tab else AppChipVariant.Filter

fun appSearchVariant(raw: String): AppSearchVariant =
    if (raw == "hero") AppSearchVariant.Hero else AppSearchVariant.Default

fun parseAspectRatio(value: String): Float {
    val parts = value.split(":")
    if (parts.size == 2) {
        val w = parts[0].toFloatOrNull()
        val h = parts[1].toFloatOrNull()
        if (w != null && h != null && h != 0f) return w / h
    }
    return 16f / 9f
}

@Composable
fun AppText(
    text: String,
    variant: AppTextVariant,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
) {
    val style = when (variant) {
        AppTextVariant.Title -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        AppTextVariant.Caption -> MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        AppTextVariant.Price -> MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        AppTextVariant.OnHero -> MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onPrimary,
        )
        AppTextVariant.OnHeroTitle -> MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        AppTextVariant.Body -> MaterialTheme.typography.bodyMedium
    }
    Text(
        text = text,
        style = style,
        modifier = modifier,
        minLines = minLines,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun AppNetworkImage(
    url: String,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun AppNamedIcon(
    name: String,
    modifier: Modifier = Modifier,
) {
    val image = when (name) {
        "location" -> Icons.Filled.LocationOn
        "search" -> Icons.Filled.Search
        "chevron" -> Icons.Filled.KeyboardArrowDown
        else -> Icons.Filled.Search
    }
    Icon(
        imageVector = image,
        contentDescription = name,
        tint = MaterialTheme.colorScheme.primary,
        modifier = modifier.size(24.dp),
    )
}

@Composable
fun AppSearchBar(
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppSearchVariant = AppSearchVariant.Default,
) {
    val hero = variant == AppSearchVariant.Hero
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (hero) Modifier.padding(end = 48.dp) else Modifier)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (hero) CarsHeroDeep
        else MaterialTheme.colorScheme.surface,
        tonalElevation = if (hero) 0.dp else 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = if (hero) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                placeholder,
                color = if (hero) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun AppFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppChipVariant = AppChipVariant.Filter,
) {
    if (variant == AppChipVariant.Tab) {
        Column(
            modifier = modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .width(if (selected) 28.dp else 0.dp)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(2.dp)),
            )
        }
    } else {
        FilterChip(
            selected = selected,
            onClick = onClick,
            label = { Text(text) },
            modifier = modifier,
        )
    }
}

@Composable
fun AppActionButton(
    text: String,
    variant: AppButtonVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (variant) {
        AppButtonVariant.Secondary -> OutlinedButton(onClick = onClick, modifier = modifier) {
            Text(text)
        }
        AppButtonVariant.Ghost -> TextButton(onClick = onClick, modifier = modifier) {
            Text(text)
        }
        AppButtonVariant.Inverse -> Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = CarsText,
            ),
        ) {
            Text(text)
        }
        AppButtonVariant.Primary -> Button(onClick = onClick, modifier = modifier) {
            Text(text)
        }
    }
}

@Composable
fun AppSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    variant: AppTextVariant = AppTextVariant.Title,
) {
    AppText(
        text = title,
        variant = variant,
        modifier = modifier.padding(horizontal = 16.dp),
    )
}

@Composable
fun AppSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    titleVariant: AppTextVariant = AppTextVariant.Title,
    actionText: String? = null,
    actionVariant: String = "link",
    onAction: (() -> Unit)? = null,
) {
    val onHero = titleVariant == AppTextVariant.OnHero || titleVariant == AppTextVariant.OnHeroTitle
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppText(title, titleVariant, Modifier.weight(1f))
        if (!actionText.isNullOrEmpty()) {
            if (actionVariant == "badge") {
                Surface(shape = RoundedCornerShape(50), color = CarsBadge) {
                    Text(
                        text = actionText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            } else {
                Text(
                    text = actionText,
                    color = if (onHero) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = if (onAction != null) Modifier.clickable(onClick = onAction) else Modifier,
                )
            }
        }
    }
}
