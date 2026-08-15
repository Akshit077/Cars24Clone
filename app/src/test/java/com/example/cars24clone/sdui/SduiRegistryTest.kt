package com.example.cars24clone.sdui

import com.example.cars24clone.sdui.registry.defaultSduiRegistry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SduiRegistryTest {

    @Test
    fun primitivesAreRegisteredAndUnknownIsNot() {
        val registry = defaultSduiRegistry()
        listOf(
            "column", "row", "spacer", "section",
            "list", "grid", "carousel",
            "text", "image", "icon", "button", "chip", "search",
            "card", "sheet", "tabs",
        ).forEach { type ->
            assertTrue(type, registry.contains(type))
        }
        assertFalse(registry.contains("liveAuctionTicker"))
        assertFalse(registry.contains("CarRail"))
    }
}
