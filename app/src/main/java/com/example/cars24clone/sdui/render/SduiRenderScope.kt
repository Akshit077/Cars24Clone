package com.example.cars24clone.sdui.render

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.example.cars24clone.sdui.model.SduiAction
import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.model.SduiNode
import com.example.cars24clone.sdui.mvi.SduiIntent
import com.example.cars24clone.sdui.registry.SduiRegistry
import com.example.cars24clone.sdui.runtime.isVisible
import kotlinx.serialization.json.JsonObject

data class SduiRenderScope(
    val document: SduiDocument,
    val nodeState: JsonObject,
    val registry: SduiRegistry,
    val onIntent: (SduiIntent) -> Unit,
) {
    fun dispatch(actions: List<SduiAction>) {
        if (actions.isEmpty()) return
        onIntent(SduiIntent.ExecuteNodeActions(actions))
    }
}

internal val LocalSduiScrollable = staticCompositionLocalOf { false }

@Composable
fun SduiNodeView(
    node: SduiNode,
    scope: SduiRenderScope,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
) {
    if (!isVisible(node.visibleIf, scope.nodeState, scope.document.lookups)) return
    CompositionLocalProvider(LocalSduiScrollable provides scrollable) {
        Box(modifier) {
            scope.registry.Render(node, scope)
        }
    }
}

@Composable
fun SduiChildren(node: SduiNode, scope: SduiRenderScope) {
    node.children.forEach { child ->
        SduiNodeView(child, scope)
    }
}
