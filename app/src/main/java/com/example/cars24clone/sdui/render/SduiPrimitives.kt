package com.example.cars24clone.sdui.render

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.cars24clone.sdui.model.SduiNode
import com.example.cars24clone.sdui.runtime.colorToken
import com.example.cars24clone.sdui.runtime.element
import com.example.cars24clone.sdui.runtime.int
import com.example.cars24clone.sdui.runtime.jsonLooseEquals
import com.example.cars24clone.sdui.runtime.resolveBind
import com.example.cars24clone.sdui.runtime.resolveBindText
import com.example.cars24clone.sdui.runtime.spaceToken
import com.example.cars24clone.sdui.runtime.string
import com.example.cars24clone.sdui.runtime.toModifier

@Composable
internal fun ColumnNode(node: SduiNode, scope: SduiRenderScope) {
    val scrollable = LocalSduiScrollable.current
    val gap = spaceToken(node.style.gap)
    val modifier = node.style.toModifier()
        .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
        .fillMaxWidth()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        SduiChildren(node, scope)
    }
}

@Composable
internal fun RowNode(node: SduiNode, scope: SduiRenderScope) {
    val gap = spaceToken(node.style.gap)
    Row(
        modifier = node.style.toModifier().fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SduiChildren(node, scope)
    }
}

@Composable
internal fun SpacerNode(node: SduiNode) {
    val size = spaceToken(node.props.string("size", node.style.height ?: "space.md"))
    Spacer(Modifier.height(size).then(node.style.toModifier()))
}

@Composable
internal fun SectionNode(node: SduiNode, scope: SduiRenderScope) {
    val title = node.props.string("title")
    Column(
        modifier = node.style.toModifier().fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spaceToken(node.style.gap ?: "space.sm")),
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = spaceToken("space.md")),
            )
        }
        SduiChildren(node, scope)
    }
}

@Composable
internal fun ListNode(node: SduiNode, scope: SduiRenderScope) {
    ColumnNode(node, scope)
}

@Composable
internal fun GridNode(node: SduiNode, scope: SduiRenderScope) {
    val columns = node.props.int("columns", 2).coerceAtLeast(1)
    val gap = spaceToken(node.style.gap)
    Column(
        modifier = node.style.toModifier().fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        node.children.chunked(columns).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                row.forEach { child ->
                    Box(Modifier.weight(1f)) {
                        SduiNodeView(child, scope)
                    }
                }
                repeat(columns - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun CarouselNode(node: SduiNode, scope: SduiRenderScope) {
    val itemWidth = node.props.int("itemWidth", 240)
    val gap = spaceToken(node.style.gap)
    LazyRow(
        modifier = node.style.toModifier().fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
        contentPadding = PaddingValues(0.dp),
    ) {
        itemsIndexed(
            items = node.children,
            key = { index, child -> child.id ?: "${node.id.orEmpty()}-$index" },
        ) { _, child ->
            Box(Modifier.width(itemWidth.dp)) {
                SduiNodeView(child, scope)
            }
        }
    }
}

@Composable
internal fun TextNode(node: SduiNode, scope: SduiRenderScope) {
    val bound = node.bind["text"]?.let {
        resolveBindText(it, scope.controller.state, scope.document.lookups)
    }
    val text = bound ?: node.props.string("text")
    val variant = node.props.string("variant", "body")
    val style = when (variant) {
        "title" -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        "caption" -> MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        "price" -> MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        else -> MaterialTheme.typography.bodyMedium
    }
    Text(text = text, style = style, modifier = node.style.toModifier())
}

@Composable
internal fun ImageNode(node: SduiNode) {
    val url = node.props.string("url")
    val ratio = parseAspectRatio(node.props.string("aspectRatio", "16:9"))
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = node.style.toModifier()
            .fillMaxWidth()
            .aspectRatio(ratio)
            .clip(RoundedCornerShape(8.dp)),
    )
}

@Composable
internal fun IconNode(node: SduiNode) {
    val name = node.props.string("name")
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
        modifier = node.style.toModifier().size(24.dp),
    )
}

@Composable
internal fun ButtonNode(node: SduiNode, scope: SduiRenderScope) {
    val text = node.props.string("text")
    val variant = node.props.string("variant", "primary")
    val onClick = { scope.controller.dispatch(node.actions) }
    val modifier = node.style.toModifier().fillMaxWidth()
    when (variant) {
        "secondary" -> OutlinedButton(onClick = onClick, modifier = modifier) { Text(text) }
        "ghost" -> TextButton(onClick = onClick, modifier = modifier) { Text(text) }
        else -> Button(onClick = onClick, modifier = modifier) { Text(text) }
    }
}

@Composable
internal fun ChipNode(node: SduiNode, scope: SduiRenderScope) {
    val text = node.props.string("text")
    val selected = node.bind["selected"]?.let { expr ->
        val actual = resolveBind(expr, scope.controller.state, scope.document.lookups)
        jsonLooseEquals(actual, node.props.element("value"))
    } ?: false
    FilterChip(
        selected = selected,
        onClick = { scope.controller.dispatch(node.actions) },
        label = { Text(text) },
        modifier = node.style.toModifier(),
    )
}

@Composable
internal fun SearchNode(node: SduiNode, scope: SduiRenderScope) {
    val placeholder = node.props.string("placeholder", "Search")
    Surface(
        modifier = node.style.toModifier()
            .fillMaxWidth()
            .clickable { scope.controller.dispatch(node.actions) },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun CardNode(node: SduiNode, scope: SduiRenderScope) {
    val container = colorToken(node.style.background).takeIf {
        it != Color.Unspecified
    } ?: MaterialTheme.colorScheme.surface
    val colors = CardDefaults.cardColors(containerColor = container)
    val modifier = node.style.toModifier().fillMaxWidth()
    if (node.actions.isNotEmpty()) {
        Card(
            onClick = { scope.controller.dispatch(node.actions) },
            modifier = modifier,
            colors = colors,
        ) {
            SduiChildren(node, scope)
        }
    } else {
        Card(modifier = modifier, colors = colors) {
            SduiChildren(node, scope)
        }
    }
}

@Composable
internal fun TabsNode(node: SduiNode, scope: SduiRenderScope) {
    Column(
        modifier = node.style.toModifier().fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spaceToken(node.style.gap)),
    ) {
        SduiChildren(node, scope)
    }
}

@Composable
internal fun FallbackNode(node: SduiNode) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Unknown component",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            Text(
                text = node.type,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            val hint = node.props.string("text")
            if (hint.isNotEmpty()) {
                Text(hint, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun parseAspectRatio(value: String): Float {
    val parts = value.split(":")
    if (parts.size == 2) {
        val w = parts[0].toFloatOrNull()
        val h = parts[1].toFloatOrNull()
        if (w != null && h != null && h != 0f) return w / h
    }
    return 16f / 9f
}
