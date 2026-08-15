package com.example.cars24clone.sdui.registry

import androidx.compose.runtime.Composable
import com.example.cars24clone.sdui.model.SduiNode
import com.example.cars24clone.sdui.render.SduiRenderScope

fun interface SduiRenderer {
    @Composable
    fun Render(node: SduiNode, scope: SduiRenderScope)
}

class SduiRegistry(
    private val fallback: SduiRenderer,
) {
    private val renderers = mutableMapOf<String, SduiRenderer>()

    fun register(type: String, renderer: SduiRenderer) {
        renderers[type] = renderer
    }

    fun contains(type: String): Boolean = renderers.containsKey(type)

    @Composable
    fun Render(node: SduiNode, scope: SduiRenderScope) {
        val renderer = renderers[node.type] ?: fallback
        renderer.Render(node, scope)
    }
}
