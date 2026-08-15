package com.example.cars24clone.sdui

import com.example.cars24clone.sdui.model.SduiAction
import com.example.cars24clone.sdui.model.SduiVisibleIf
import com.example.cars24clone.sdui.runtime.applyActions
import com.example.cars24clone.sdui.runtime.isVisible
import com.example.cars24clone.sdui.runtime.resolveBindText
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SduiBindingsTest {

    private val lookups = JsonObject(
        mapOf(
            "emiByTenure" to JsonObject(
                mapOf(
                    "12" to JsonPrimitive("₹8,499/mo"),
                    "24" to JsonPrimitive("₹4,599/mo"),
                ),
            ),
        ),
    )

    @Test
    fun lookupUsesStringifiedStateNumber() {
        val state = JsonObject(mapOf("tenureMonths" to JsonPrimitive(24)))
        assertEquals(
            "₹4,599/mo",
            resolveBindText("lookups.emiByTenure[state.tenureMonths]", state, lookups),
        )
    }

    @Test
    fun setStateThenLookupUpdates() {
        val initial = JsonObject(mapOf("tenureMonths" to JsonPrimitive(24)))
        val result = applyActions(
            initial,
            listOf(SduiAction(type = "setState", path = "tenureMonths", value = JsonPrimitive(12))),
        )
        assertEquals(
            "₹8,499/mo",
            resolveBindText("lookups.emiByTenure[state.tenureMonths]", result.state, lookups),
        )
    }

    @Test
    fun visibleIfEqAndMissingPath() {
        val state = JsonObject(mapOf("selectedCategory" to JsonPrimitive("suv")))
        assertTrue(
            isVisible(
                SduiVisibleIf(path = "state.selectedCategory", eq = JsonPrimitive("suv")),
                state,
                JsonObject(emptyMap()),
            ),
        )
        assertFalse(
            isVisible(
                SduiVisibleIf(path = "state.missing", eq = JsonPrimitive("suv")),
                state,
                JsonObject(emptyMap()),
            ),
        )
    }

    @Test
    fun unknownActionIsIgnored() {
        val state = JsonObject(mapOf("tenureMonths" to JsonPrimitive(24)))
        val result = applyActions(state, listOf(SduiAction(type = "launchRocket")))
        assertEquals(state, result.state)
        assertNull(result.navigationUrl)
        assertNull(result.openSheetId)
        assertFalse(result.closeSheet)
    }
}
