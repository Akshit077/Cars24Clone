package com.example.cars24clone.sdui.registry

import com.example.cars24clone.sdui.render.ButtonNode
import com.example.cars24clone.sdui.render.CardNode
import com.example.cars24clone.sdui.render.CarouselNode
import com.example.cars24clone.sdui.render.ChipNode
import com.example.cars24clone.sdui.render.ColumnNode
import com.example.cars24clone.sdui.render.FallbackNode
import com.example.cars24clone.sdui.render.GridNode
import com.example.cars24clone.sdui.render.IconNode
import com.example.cars24clone.sdui.render.ImageNode
import com.example.cars24clone.sdui.render.ListNode
import com.example.cars24clone.sdui.render.RowNode
import com.example.cars24clone.sdui.render.SearchNode
import com.example.cars24clone.sdui.render.SectionNode
import com.example.cars24clone.sdui.render.SpacerNode
import com.example.cars24clone.sdui.render.TabsNode
import com.example.cars24clone.sdui.render.TextNode

fun defaultSduiRegistry(): SduiRegistry {
    val registry = SduiRegistry(fallback = SduiRenderer { node, _ -> FallbackNode(node) })
    registry.register("column") { node, scope -> ColumnNode(node, scope) }
    registry.register("row") { node, scope -> RowNode(node, scope) }
    registry.register("spacer") { node, _ -> SpacerNode(node) }
    registry.register("section") { node, scope -> SectionNode(node, scope) }
    registry.register("list") { node, scope -> ListNode(node, scope) }
    registry.register("grid") { node, scope -> GridNode(node, scope) }
    registry.register("carousel") { node, scope -> CarouselNode(node, scope) }
    registry.register("text") { node, scope -> TextNode(node, scope) }
    registry.register("image") { node, _ -> ImageNode(node) }
    registry.register("icon") { node, _ -> IconNode(node) }
    registry.register("button") { node, scope -> ButtonNode(node, scope) }
    registry.register("chip") { node, scope -> ChipNode(node, scope) }
    registry.register("search") { node, scope -> SearchNode(node, scope) }
    registry.register("card") { node, scope -> CardNode(node, scope) }
    registry.register("sheet") { node, scope -> ColumnNode(node, scope) }
    registry.register("tabs") { node, scope -> TabsNode(node, scope) }
    return registry
}
