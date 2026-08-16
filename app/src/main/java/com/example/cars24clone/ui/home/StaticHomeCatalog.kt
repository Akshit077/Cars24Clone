package com.example.cars24clone.ui.home

enum class StaticTab(val id: String, val label: String) {
    All("all", "All"),
    Buy("buy", "Buy used car"),
    Sell("sell", "Sell car"),
    Loans("loans", "Loans"),
}

enum class StaticUsedFilter(val id: String, val label: String) {
    Recent("recent", "Recently viewed"),
    Deals("deals", "Hot deals"),
}

data class StaticCar(
    val id: String,
    val title: String,
    val meta: String,
    val price: String,
    val imageUrl: String,
    val route: String,
    val badge: String? = null,
)

data class StaticServiceTile(
    val title: String,
    val imageUrl: String,
    val route: String,
)

data class StaticShowroom(
    val name: String,
    val inventory: String,
    val imageUrl: String,
    val route: String,
)

data class StaticPromoCard(
    val title: String,
    val imageUrl: String,
    val route: String,
)

data class StaticLoanItem(
    val title: String,
    val imageUrl: String,
    val route: String,
)

private fun carPhoto(id: String, width: Int, height: Int): String =
    "https://images.unsplash.com/$id?auto=format&fit=crop&w=$width&h=$height&q=80"

object StaticHomeCatalog {
    val emiByTenure: Map<Int, String> = mapOf(
        12 to "₹8,499/mo",
        24 to "₹4,599/mo",
        36 to "₹3,299/mo",
    )

    const val orbitImage: String =
        "https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?auto=format&fit=crop&w=1200&h=675&q=80"

    val manageTiles: List<StaticServiceTile> = listOf(
        StaticServiceTile("Pay challan", carPhoto("photo-1568605117036-5fe5e7bab0b7", 400, 400), "sdui://challan"),
        StaticServiceTile("Recharge FASTag", carPhoto("photo-1449965404019-0194382f21d4", 400, 400), "sdui://fastag"),
        StaticServiceTile("Get insurance", carPhoto("photo-1533473359331-0135ef1b58bf", 400, 400), "sdui://insurance"),
        StaticServiceTile("Cash against car", carPhoto("photo-1553440569-bcc63803a83d", 400, 400), "sdui://cash-against-car"),
        StaticServiceTile("Road side assistance", carPhoto("photo-1486262715619-67b85e0b08d3", 400, 400), "sdui://rsa"),
        StaticServiceTile("Get warranty", carPhoto("photo-1542362567-b07e54358753", 400, 400), "sdui://warranty"),
    )

    val showrooms: List<StaticShowroom> = listOf(
        StaticShowroom("Wave One Mall", "150+ cars", carPhoto("photo-1492144534655-ae79c964c9d7", 800, 600), "sdui://showroom/wave-one"),
        StaticShowroom("DLF Cyber Hub", "80+ cars", carPhoto("photo-1544636331-e26879cd4d9b", 800, 600), "sdui://showroom/dlf"),
        StaticShowroom("Ambience Mall", "10+ cars", carPhoto("photo-1503376780353-7e6692767b70", 800, 600), "sdui://showroom/ambience"),
    )

    val buyCards: List<StaticPromoCard> = listOf(
        StaticPromoCard("All used cars", carPhoto("photo-1552519507-da3b142c6e3d", 600, 450), "sdui://buy/all"),
        StaticPromoCard("Budget used cars", carPhoto("photo-1549317661-bd32c8ce0db2", 600, 450), "sdui://buy/budget"),
        StaticPromoCard("Premium used cars", carPhoto("photo-1583121274602-3e2820c69888", 600, 450), "sdui://buy/premium"),
    )

    val sellCards: List<StaticPromoCard> = listOf(
        StaticPromoCard("Sell your car", carPhoto("photo-1489824904134-891ab64532f1", 600, 450), "sdui://sell"),
        StaticPromoCard("Check car valuation", carPhoto("photo-1605559424843-9e4c228bf1c2", 600, 450), "sdui://valuation"),
        StaticPromoCard("Scrap your car", carPhoto("photo-1486262715619-67b85e0b08d3", 600, 450), "sdui://scrap"),
    )

    val loans: List<StaticLoanItem> = listOf(
        StaticLoanItem("Used car loan", carPhoto("photo-1552519507-da3b142c6e3d", 300, 300), "sdui://loan/used-car"),
        StaticLoanItem("Loan against car", carPhoto("photo-1618843479313-40f8afb4b4d8", 300, 300), "sdui://loan/against-car"),
        StaticLoanItem("Personal loan", carPhoto("photo-1502877338535-766e1452684a", 300, 300), "sdui://loan/personal"),
        StaticLoanItem("Credit score", carPhoto("photo-1542282088-fe8426682b8f", 300, 300), "sdui://credit-score"),
    )

    val carChecks: List<StaticServiceTile> = listOf(
        StaticServiceTile("New car PDI", carPhoto("photo-1494976388531-d1058494cdd8", 400, 400), "sdui://check/pdi"),
        StaticServiceTile("Used car check", carPhoto("photo-1486262715619-67b85e0b08d3", 400, 400), "sdui://check/used"),
        StaticServiceTile("Vehicle history", carPhoto("photo-1549317661-bd32c8ce0db2", 400, 400), "sdui://check/history"),
        StaticServiceTile("Check challan", carPhoto("photo-1568605117036-5fe5e7bab0b7", 400, 400), "sdui://challan"),
        StaticServiceTile("Check car insurance", carPhoto("photo-1533473359331-0135ef1b58bf", 400, 400), "sdui://insurance"),
        StaticServiceTile("Odometer tampering", carPhoto("photo-1449965404019-0194382f21d4", 400, 400), "sdui://check/odometer"),
    )

    val recentCars: List<StaticCar> = listOf(
        StaticCar(
            id = "kushaq-2023",
            title = "2023 Skoda KUSHAQ",
            meta = "Active plus 1.0 TSI",
            price = "₹12.40 Lakh",
            imageUrl = carPhoto("photo-1519641471654-76ce0107ad1b", 800, 600),
            route = "sdui://car/kushaq-2023",
            badge = "Cars24 Owned stock",
        ),
        StaticCar(
            id = "creta-2021",
            title = "2021 Hyundai Creta SX",
            meta = "28,400 km · Diesel · Auto",
            price = "₹12.40 Lakh",
            imageUrl = carPhoto("photo-1533473359331-0135ef1b58bf", 800, 600),
            route = "sdui://car/creta-2021",
        ),
    )

    val dealCars: List<StaticCar> = listOf(
        StaticCar(
            id = "swift-2019",
            title = "2019 Maruti Swift VXI",
            meta = "42,000 km · Petrol · Manual",
            price = "₹4.85 Lakh",
            imageUrl = carPhoto("photo-1549317661-bd32c8ce0db2", 800, 600),
            route = "sdui://car/swift-2019",
            badge = "Hot deal",
        ),
        StaticCar(
            id = "city-2020",
            title = "2020 Honda City ZX",
            meta = "31,200 km · Petrol · Auto",
            price = "₹9.10 Lakh",
            imageUrl = carPhoto("photo-1494976388531-d1058494cdd8", 800, 600),
            route = "sdui://car/city-2020",
            badge = "Hot deal",
        ),
    )

    fun carsFor(filter: StaticUsedFilter): List<StaticCar> =
        if (filter == StaticUsedFilter.Recent) recentCars else dealCars
}
