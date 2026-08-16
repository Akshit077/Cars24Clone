package com.example.cars24clone.sdui

import com.example.cars24clone.sdui.asset.SduiPayload
import com.example.cars24clone.sdui.model.SduiAction
import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.model.SduiNode
import com.example.cars24clone.sdui.model.SduiScreenMeta
import com.example.cars24clone.sdui.mvi.SduiEffect
import com.example.cars24clone.sdui.mvi.SduiIntent
import com.example.cars24clone.sdui.mvi.SduiUiState
import com.example.cars24clone.sdui.mvi.reduce
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SduiReducerTest {

    private val document = SduiDocument(
        schemaVersion = 1,
        screen = SduiScreenMeta(id = "home", title = "Cars24"),
        state = JsonObject(mapOf("tenureMonths" to JsonPrimitive(24))),
        root = SduiNode(type = "column"),
    )

    @Test
    fun documentLoadedCopiesPayloadState() {
        val result = reduce(
            SduiUiState(),
            SduiIntent.DocumentLoaded(SduiPayload.Home, document),
        )
        assertEquals(document, result.state.document)
        assertEquals(24, (result.state.nodeState["tenureMonths"] as JsonPrimitive).content.toInt())
        assertNull(result.state.openSheetId)
        assertNull(result.effect)
    }

    @Test
    fun setStateAndOpenSheetStayInOneReduce() {
        val loaded = reduce(
            SduiUiState(),
            SduiIntent.DocumentLoaded(SduiPayload.Home, document),
        ).state
        val result = reduce(
            loaded,
            SduiIntent.ExecuteNodeActions(
                listOf(
                    SduiAction(type = "setState", path = "tenureMonths", value = JsonPrimitive(12)),
                    SduiAction(type = "openSheet", id = "loanSheet"),
                ),
            ),
        )
        assertEquals("12", (result.state.nodeState["tenureMonths"] as JsonPrimitive).content)
        assertEquals("loanSheet", result.state.openSheetId)
        assertNull(result.effect)
    }

    @Test
    fun navigateIsAnEffectNotState() {
        val loaded = reduce(
            SduiUiState(),
            SduiIntent.DocumentLoaded(SduiPayload.Home, document),
        ).state
        val result = reduce(
            loaded,
            SduiIntent.ExecuteNodeActions(
                listOf(SduiAction(type = "navigate", url = "sdui://car/swift-2019")),
            ),
        )
        assertEquals(SduiEffect.ShowNavigation("sdui://car/swift-2019"), result.effect)
        assertEquals(loaded.nodeState, result.state.nodeState)
    }

    @Test
    fun dismissSheetClearsId() {
        val open = SduiUiState(openSheetId = "loanSheet")
        val result = reduce(open, SduiIntent.DismissSheet)
        assertNull(result.state.openSheetId)
    }

    @Test
    fun unknownActionDoesNotChangeState() {
        val loaded = reduce(
            SduiUiState(),
            SduiIntent.DocumentLoaded(SduiPayload.Home, document),
        ).state
        val result = reduce(
            loaded,
            SduiIntent.ExecuteNodeActions(listOf(SduiAction(type = "launchRocket"))),
        )
        assertEquals(loaded, result.state)
        assertNull(result.effect)
    }

    @Test
    fun loadFailedClearsDocument() {
        val loaded = reduce(
            SduiUiState(),
            SduiIntent.DocumentLoaded(SduiPayload.Home, document),
        ).state
        val result = reduce(
            loaded,
            SduiIntent.LoadFailed(SduiPayload.UnknownType, "missing"),
        )
        assertNull(result.state.document)
        assertTrue(result.state.nodeState.isEmpty())
        assertEquals("missing", result.state.loadError)
    }
}
