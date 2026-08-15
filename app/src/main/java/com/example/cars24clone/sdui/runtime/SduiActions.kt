package com.example.cars24clone.sdui.runtime

import com.example.cars24clone.sdui.model.SduiAction
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

data class SduiActionResult(
    val state: JsonObject,
    val navigationUrl: String? = null,
    val openSheetId: String? = null,
    val closeSheet: Boolean = false,
)

fun applyActions(state: JsonObject, actions: List<SduiAction>): SduiActionResult {
    var next = state
    var navigationUrl: String? = null
    var openSheetId: String? = null
    var closeSheet = false
    for (action in actions) {
        when (action.type) {
            "setState" -> {
                val path = action.path ?: continue
                val value = action.value ?: continue
                next = writeState(next, path, value)
            }
            "navigate" -> navigationUrl = action.url
            "openSheet" -> openSheetId = action.id
            "closeSheet" -> closeSheet = true
            else -> Unit
        }
    }
    return SduiActionResult(
        state = next,
        navigationUrl = navigationUrl,
        openSheetId = openSheetId,
        closeSheet = closeSheet,
    )
}

fun writeState(state: JsonObject, path: String, value: JsonElement): JsonObject {
    val key = path.removePrefix("state.").substringAfterLast('.')
    if (key.isEmpty()) return state
    return JsonObject(state + (key to value))
}
