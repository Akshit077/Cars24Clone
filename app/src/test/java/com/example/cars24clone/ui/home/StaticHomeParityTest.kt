package com.example.cars24clone.ui.home

import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.model.SduiJson
import com.example.cars24clone.sdui.model.walkNodes
import com.example.cars24clone.sdui.runtime.string
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * The static twin is only useful for overhead % if it shows the same page.
 */
class StaticHomeParityTest {

    @Test
    fun catalogCopyAndAssetsAppearInHomeJson() {
        val doc = decodeHome()
        val texts = doc.walkNodes().flatMap { node ->
            listOf(
                node.props.string("text"),
                node.props.string("title"),
                node.props.string("placeholder"),
                node.props.string("actionText"),
            )
        }.filter { it.isNotEmpty() }.toSet()
        val urls = doc.walkNodes().map { it.props.string("url") }.filter { it.isNotEmpty() }.toSet()
        val routes = doc.walkNodes()
            .flatMap { node -> node.actions.mapNotNull { it.url } }
            .toSet()

        val requiredCopy = listOf(
            "Search Swift",
            "Manage your vehicle",
            "Offers for you",
            "Add your car to Orbit",
            "3 showrooms in your city",
            "Buy car",
            "Sell your car",
            "Get loans",
            "Car check services",
            "Used cars you'll love",
            "Car loan EMI",
            "Check eligibility",
        )
        requiredCopy.forEach { copy ->
            assertTrue("home.json missing copy: $copy", copy in texts)
        }

        val catalogImages = buildList {
            add(StaticHomeCatalog.orbitImage)
            addAll(StaticHomeCatalog.manageTiles.map { it.imageUrl })
            addAll(StaticHomeCatalog.showrooms.map { it.imageUrl })
            addAll(StaticHomeCatalog.buyCards.map { it.imageUrl })
            addAll(StaticHomeCatalog.sellCards.map { it.imageUrl })
            addAll(StaticHomeCatalog.loans.map { it.imageUrl })
            addAll(StaticHomeCatalog.carChecks.map { it.imageUrl })
            addAll(StaticHomeCatalog.recentCars.map { it.imageUrl })
            addAll(StaticHomeCatalog.dealCars.map { it.imageUrl })
        }
        catalogImages.forEach { url ->
            assertTrue("home.json missing catalog image: $url", url in urls)
        }

        val catalogRoutes = buildList {
            addAll(StaticHomeCatalog.manageTiles.map { it.route })
            addAll(StaticHomeCatalog.showrooms.map { it.route })
            addAll(StaticHomeCatalog.buyCards.map { it.route })
            addAll(StaticHomeCatalog.sellCards.map { it.route })
            addAll(StaticHomeCatalog.loans.map { it.route })
            addAll(StaticHomeCatalog.carChecks.map { it.route })
            addAll(StaticHomeCatalog.recentCars.map { it.route })
            addAll(StaticHomeCatalog.dealCars.map { it.route })
        }
        catalogRoutes.forEach { route ->
            assertTrue("home.json missing catalog route: $route", route in routes)
        }
    }

    private fun decodeHome(): SduiDocument {
        val file = listOf(
            File("src/main/assets/sdui/home.json"),
            File("app/src/main/assets/sdui/home.json"),
        ).first { it.exists() }
        return SduiJson.decodeFromString(SduiDocument.serializer(), file.readText())
    }
}
