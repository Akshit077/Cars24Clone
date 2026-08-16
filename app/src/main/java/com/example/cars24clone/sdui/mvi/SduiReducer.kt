package com.example.cars24clone.sdui.mvi

import com.example.cars24clone.sdui.runtime.applyActions
import kotlinx.serialization.json.JsonObject

fun reduce(state: SduiUiState, intent: SduiIntent): SduiReduceResult {
    return when (intent) {
        is SduiIntent.SelectPayload -> SduiReduceResult(
            state.copy(
                payload = intent.payload,
                loadError = null,
            ),
        )
        is SduiIntent.DocumentLoaded -> SduiReduceResult(
            state.copy(
                payload = intent.payload,
                document = intent.document,
                nodeState = intent.document.state,
                openSheetId = null,
                loadError = null,
            ),
        )
        is SduiIntent.LoadFailed -> SduiReduceResult(
            state.copy(
                payload = intent.payload,
                document = null,
                nodeState = JsonObject(emptyMap()),
                openSheetId = null,
                loadError = intent.message,
            ),
        )
        is SduiIntent.ExecuteNodeActions -> reduceNodeActions(state, intent)
        SduiIntent.DismissSheet -> SduiReduceResult(state.copy(openSheetId = null))
    }
}

private fun reduceNodeActions(
    state: SduiUiState,
    intent: SduiIntent.ExecuteNodeActions,
): SduiReduceResult {
    if (intent.actions.isEmpty()) return SduiReduceResult(state)
    val result = applyActions(state.nodeState, intent.actions)
    val openSheetId = when {
        result.closeSheet -> null
        result.openSheetId != null -> result.openSheetId
        else -> state.openSheetId
    }
    val effect = result.navigationUrl?.let { SduiEffect.ShowNavigation(it) }
    return SduiReduceResult(
        state = state.copy(nodeState = result.state, openSheetId = openSheetId),
        effect = effect,
    )
}
