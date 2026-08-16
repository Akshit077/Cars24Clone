package com.example.cars24clone.sdui.mvi

import com.example.cars24clone.sdui.asset.SduiPayload
import com.example.cars24clone.sdui.model.SduiAction
import com.example.cars24clone.sdui.model.SduiDocument
import kotlinx.serialization.json.JsonObject

sealed interface SduiIntent {
    data class SelectPayload(val payload: SduiPayload) : SduiIntent
    data class DocumentLoaded(val payload: SduiPayload, val document: SduiDocument) : SduiIntent
    data class LoadFailed(val payload: SduiPayload, val message: String) : SduiIntent
    data class ExecuteNodeActions(val actions: List<SduiAction>) : SduiIntent
    data object DismissSheet : SduiIntent
}

data class SduiUiState(
    val payload: SduiPayload = SduiPayload.Home,
    val document: SduiDocument? = null,
    val nodeState: JsonObject = JsonObject(emptyMap()),
    val openSheetId: String? = null,
    val loadError: String? = null,
)

sealed interface SduiEffect {
    data class ShowNavigation(val url: String) : SduiEffect
}

data class SduiReduceResult(
    val state: SduiUiState,
    val effect: SduiEffect? = null,
)
