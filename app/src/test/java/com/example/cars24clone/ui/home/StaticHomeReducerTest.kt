package com.example.cars24clone.ui.home

import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.model.SduiJson
import com.example.cars24clone.sdui.runtime.resolveBindText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class StaticHomeReducerTest {

    @Test
    fun tenureUpdatesEmiFromCatalog() {
        val selected = reduceStaticHome(
            StaticHomeUiState(),
            StaticHomeIntent.SelectTenure(12),
        )
        assertEquals(12, selected.state.tenureMonths)
        assertEquals("₹8,499/mo", selected.state.emi)
        assertNull(selected.effect)
    }

    @Test
    fun tabHidesUnrelatedSections() {
        val buy = reduceStaticHome(
            StaticHomeUiState(),
            StaticHomeIntent.SelectTab(StaticTab.Buy),
        ).state
        assertEquals(StaticTab.Buy, buy.tab)
        assertFalse(buy.showManage)
        assertTrue(buy.showBuy)
        assertTrue(buy.showUsedCars)
        assertFalse(buy.showSell)
        assertFalse(buy.showFinance)
    }

    @Test
    fun usedCarFilterSwapsRail() {
        val deals = reduceStaticHome(
            StaticHomeUiState(),
            StaticHomeIntent.SelectUsedFilter(StaticUsedFilter.Deals),
        )
        assertEquals(listOf("swift-2019", "city-2020"), deals.state.cars.map { it.id })
    }

    @Test
    fun navigateIsAnEffect() {
        val result = reduceStaticHome(
            StaticHomeUiState(),
            StaticHomeIntent.Navigate("sdui://car/kushaq-2023"),
        )
        assertEquals(StaticHomeEffect.ShowNavigation("sdui://car/kushaq-2023"), result.effect)
    }

    @Test
    fun staticEmiMatchesHomeJsonLookups() {
        val file = listOf(
            File("src/main/assets/sdui/home.json"),
            File("app/src/main/assets/sdui/home.json"),
        ).first { it.exists() }
        val doc = SduiJson.decodeFromString(SduiDocument.serializer(), file.readText())
        StaticHomeCatalog.emiByTenure.forEach { (months, label) ->
            val state = reduceStaticHome(
                StaticHomeUiState(),
                StaticHomeIntent.SelectTenure(months),
            ).state
            val fromJson = resolveBindText(
                "lookups.emiByTenure[state.tenureMonths]",
                kotlinx.serialization.json.JsonObject(
                    mapOf("tenureMonths" to kotlinx.serialization.json.JsonPrimitive(months)),
                ),
                doc.lookups,
            )
            assertEquals(fromJson, state.emi)
            assertEquals(label, state.emi)
        }
    }
}
