package com.example.cars24clone.sdui

import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.model.SduiJson
import com.example.cars24clone.sdui.model.walkNodes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SduiDocumentParseTest {

    @Test
    fun homeJsonMeetsComplexityBar() {
        val doc = decode("home.json")
        val types = doc.walkNodes().map { it.type }.toSet()

        assertEquals(1, doc.schemaVersion)
        assertEquals("home", doc.screen.id)
        assertEquals("column", doc.root.type)
        assertTrue(types.containsAll(listOf("carousel", "grid", "chip", "card", "search")))
        assertTrue(doc.walkNodes().any { node -> node.actions.any { it.type == "setState" } })
        assertTrue(doc.walkNodes().any { node -> node.actions.any { it.type == "openSheet" } })
        assertTrue(doc.sheets.containsKey("loanSheet"))
        assertTrue(doc.walkNodes().any { it.bind.containsKey("text") })
        assertTrue(doc.walkNodes().none { it.type in PAGE_SPECIFIC_TYPES })
    }

    @Test
    fun unknownTypeIsPreservedForFallback() {
        val doc = decode("home_unknown_type.json")
        assertTrue(doc.walkNodes().any { it.type == "liveAuctionTicker" })
        assertTrue(doc.walkNodes().any { it.type == "card" })
    }

    @Test
    fun carDetailSketchUsesSamePrimitives() {
        val doc = decode("car_detail_sketch.json")
        assertEquals("car-detail", doc.screen.id)
        assertTrue(doc.walkNodes().none { it.type in PAGE_SPECIFIC_TYPES })
        assertTrue(doc.walkNodes().any { it.actions.any { action -> action.type == "openSheet" } })
    }

    private fun decode(name: String): SduiDocument {
        val file = assetFile(name)
        assertTrue("Missing $name at ${file.path}", file.exists())
        return SduiJson.decodeFromString(SduiDocument.serializer(), file.readText())
    }

    private fun assetFile(name: String): File {
        val candidates = listOf(
            File("src/main/assets/sdui/$name"),
            File("app/src/main/assets/sdui/$name"),
        )
        return candidates.firstOrNull { it.exists() } ?: candidates.first()
    }

    private companion object {
        val PAGE_SPECIFIC_TYPES = setOf(
            "HomeBanner",
            "CarRail",
            "CarCard",
            "CategoryChips",
        )
    }
}
