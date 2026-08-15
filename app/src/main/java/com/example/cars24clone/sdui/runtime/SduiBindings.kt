package com.example.cars24clone.sdui.runtime

import com.example.cars24clone.sdui.model.SduiVisibleIf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

private val STATE_PATH = Regex("""^state\.([A-Za-z0-9_]+)$""")
private val LOOKUP_PATH = Regex("""^lookups\.([A-Za-z0-9_]+)\[state\.([A-Za-z0-9_]+)\]$""")

fun resolveBind(expr: String, state: JsonObject, lookups: JsonObject): JsonElement? {
    STATE_PATH.matchEntire(expr)?.let { match ->
        return state[match.groupValues[1]]
    }
    LOOKUP_PATH.matchEntire(expr)?.let { match ->
        val table = lookups[match.groupValues[1]]?.jsonObject ?: return null
        val key = state[match.groupValues[2]]?.let { (it as? JsonPrimitive)?.content } ?: return null
        return table[key]
    }
    return null
}

fun resolveBindText(expr: String, state: JsonObject, lookups: JsonObject): String? {
    val element = resolveBind(expr, state, lookups) ?: return null
    return (element as? JsonPrimitive)?.contentOrNull
}

fun isVisible(visibleIf: SduiVisibleIf?, state: JsonObject, lookups: JsonObject): Boolean {
    if (visibleIf == null) return true
    val actual = resolveBind(visibleIf.path, state, lookups) ?: return false
    return when {
        visibleIf.eq != null -> jsonLooseEquals(actual, visibleIf.eq)
        visibleIf.inValues != null -> visibleIf.inValues.any { jsonLooseEquals(actual, it) }
        visibleIf.neq != null -> !jsonLooseEquals(actual, visibleIf.neq)
        else -> false
    }
}
