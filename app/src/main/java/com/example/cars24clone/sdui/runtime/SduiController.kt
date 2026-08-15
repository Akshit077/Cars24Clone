package com.example.cars24clone.sdui.runtime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cars24clone.sdui.model.SduiAction
import com.example.cars24clone.sdui.model.SduiDocument
import kotlinx.serialization.json.JsonObject

class SduiController(document: SduiDocument) {
    var state: JsonObject by mutableStateOf(document.state)
        private set
    var openSheetId: String? by mutableStateOf(null)
        private set
    var navigationUrl: String? by mutableStateOf(null)
        private set

    fun dispatch(actions: List<SduiAction>) {
        if (actions.isEmpty()) return
        val result = applyActions(state, actions)
        state = result.state
        if (result.navigationUrl != null) navigationUrl = result.navigationUrl
        if (result.openSheetId != null) openSheetId = result.openSheetId
        if (result.closeSheet) openSheetId = null
    }

    fun dismissSheet() {
        openSheetId = null
    }

    fun consumeNavigation() {
        navigationUrl = null
    }
}
