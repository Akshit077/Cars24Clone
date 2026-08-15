package com.example.cars24clone.sdui.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Wire contract for a screen. Unknown keys are ignored so old clients can
 * read newer payloads; unknown [SduiNode.type] is kept as data for the fallback renderer.
 */
@kotlinx.serialization.Serializable
data class SduiDocument(
    val schemaVersion: Int,
    val minClientVersion: Int = 1,
    val screen: SduiScreenMeta = SduiScreenMeta(),
    val state: JsonObject = JsonObject(emptyMap()),
    val lookups: JsonObject = JsonObject(emptyMap()),
    val sheets: Map<String, SduiNode> = emptyMap(),
    val root: SduiNode,
)

@kotlinx.serialization.Serializable
data class SduiScreenMeta(
    val id: String = "",
    val title: String = "",
)

@kotlinx.serialization.Serializable
data class SduiNode(
    val id: String? = null,
    val type: String,
    val props: JsonObject = JsonObject(emptyMap()),
    val style: SduiStyle = SduiStyle(),
    val bind: Map<String, String> = emptyMap(),
    val visibleIf: SduiVisibleIf? = null,
    val actions: List<SduiAction> = emptyList(),
    val children: List<SduiNode> = emptyList(),
)

@kotlinx.serialization.Serializable
data class SduiStyle(
    val padding: String? = null,
    val paddingH: String? = null,
    val paddingV: String? = null,
    val gap: String? = null,
    val background: String? = null,
    val corner: String? = null,
    val width: String? = null,
    val height: String? = null,
)

@kotlinx.serialization.Serializable
data class SduiVisibleIf(
    val path: String,
    val eq: JsonElement? = null,
    val neq: JsonElement? = null,
    @SerialName("in") val inValues: List<JsonElement>? = null,
)

@kotlinx.serialization.Serializable
data class SduiAction(
    val type: String,
    val path: String? = null,
    val value: JsonElement? = null,
    val url: String? = null,
    val id: String? = null,
)

val SduiJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

fun SduiNode.walk(): Sequence<SduiNode> = sequence {
    yield(this@walk)
    children.forEach { child -> yieldAll(child.walk()) }
}

fun SduiDocument.walkNodes(): Sequence<SduiNode> = sequence {
    yieldAll(root.walk())
    sheets.values.forEach { yieldAll(it.walk()) }
}
