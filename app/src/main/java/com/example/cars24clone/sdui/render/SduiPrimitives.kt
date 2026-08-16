package com.example.cars24clone.sdui.render

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cars24clone.sdui.model.SduiNode
import com.example.cars24clone.ui.components.AppActionButton
import com.example.cars24clone.ui.components.AppFilterChip
import com.example.cars24clone.ui.components.AppNamedIcon
import com.example.cars24clone.ui.components.AppNetworkImage
import com.example.cars24clone.ui.components.AppSearchBar
import com.example.cars24clone.ui.components.AppSectionHeader
import com.example.cars24clone.ui.components.AppText
import com.example.cars24clone.ui.components.appButtonVariant
import com.example.cars24clone.ui.components.appChipVariant
import com.example.cars24clone.ui.components.appSearchVariant
import com.example.cars24clone.ui.components.appTextVariant
import com.example.cars24clone.ui.components.parseAspectRatio
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
        .then(
            if (!scrollable && node.actions.isNotEmpty()) {
                Modifier.clickable { scope.dispatch(node.actions) }
            } else {
                Modifier
            },
        )
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
    val actionText = node.props.string("actionText")
    Column(
        modifier = node.style.toModifier().fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spaceToken(node.style.gap ?: "space.sm")),
    ) {
        if (title.isNotEmpty()) {
            AppSectionHeader(
                title = title,
                titleVariant = appTextVariant(node.props.string("titleVariant", "title")),
                actionText = actionText.ifEmpty { null },
                actionVariant = node.props.string("actionVariant", "link"),
                onAction = if (node.actions.isNotEmpty()) {
                    { scope.dispatch(node.actions) }
                } else {
                    null
                },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(gap),
            ) {
                row.forEach { child ->
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        SduiNodeView(child, scope, modifier = Modifier.fillMaxSize())
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
        resolveBindText(it, scope.nodeState, scope.document.lookups)
    }
    val text = bound ?: node.props.string("text")
    AppText(
        text = text,
        variant = appTextVariant(node.props.string("variant", "body")),
        modifier = node.style.toModifier(),
        minLines = node.props.int("minLines", 1).coerceAtLeast(1),
        maxLines = node.props.int("maxLines", Int.MAX_VALUE).coerceAtLeast(1),
    )
}

@Composable
internal fun ImageNode(node: SduiNode) {
    val scale = if (node.props.string("scale", "crop") == "fit") {
        ContentScale.Fit
    } else {
        ContentScale.Crop
    }
    AppNetworkImage(
        url = node.props.string("url"),
        aspectRatio = parseAspectRatio(node.props.string("aspectRatio", "16:9")),
        modifier = node.style.toModifier(),
        contentScale = scale,
    )
}

@Composable
internal fun IconNode(node: SduiNode) {
    AppNamedIcon(
        name = node.props.string("name"),
        modifier = node.style.toModifier(),
    )
}

@Composable
internal fun ButtonNode(node: SduiNode, scope: SduiRenderScope) {
    AppActionButton(
        text = node.props.string("text"),
        variant = appButtonVariant(node.props.string("variant", "primary")),
        onClick = { scope.dispatch(node.actions) },
        modifier = node.style.toModifier(),
    )
}

@Composable
internal fun ChipNode(node: SduiNode, scope: SduiRenderScope) {
    val text = node.props.string("text")
    val selected = node.bind["selected"]?.let { expr ->
        val actual = resolveBind(expr, scope.nodeState, scope.document.lookups)
        jsonLooseEquals(actual, node.props.element("value"))
    } ?: false
    AppFilterChip(
        text = text,
        selected = selected,
        onClick = { scope.dispatch(node.actions) },
        modifier = node.style.toModifier(),
        variant = appChipVariant(node.props.string("variant", "filter")),
    )
}

@Composable
internal fun SearchNode(node: SduiNode, scope: SduiRenderScope) {
    AppSearchBar(
        placeholder = node.props.string("placeholder", "Search"),
        onClick = { scope.dispatch(node.actions) },
        modifier = node.style.toModifier(),
        variant = appSearchVariant(node.props.string("variant", "default")),
    )
}

@Composable
internal fun CardNode(node: SduiNode, scope: SduiRenderScope) {
    val container = colorToken(node.style.background).takeIf {
        it != Color.Unspecified
    } ?: MaterialTheme.colorScheme.surface
    val colors = CardDefaults.cardColors(containerColor = container)
    val modifier = node.style.toModifier().fillMaxWidth().fillMaxHeight()
    if (node.actions.isNotEmpty()) {
        Card(
            onClick = { scope.dispatch(node.actions) },
            modifier = modifier,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            SduiChildren(node, scope)
        }
    } else {
        Card(
            modifier = modifier,
            colors = colors,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
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

