package com.example.cars24clone.ui.home

data class StaticHomeUiState(
    val tab: StaticTab = StaticTab.All,
    val usedCarFilter: StaticUsedFilter = StaticUsedFilter.Recent,
    val tenureMonths: Int = 24,
    val sheetOpen: Boolean = false,
    val addVehicleSheetOpen: Boolean = false,
) {
    val emi: String = StaticHomeCatalog.emiByTenure[tenureMonths] ?: "₹4,599/mo"
    val cars: List<StaticCar> = StaticHomeCatalog.carsFor(usedCarFilter)
    val showManage: Boolean = tab == StaticTab.All
    val showOrbit: Boolean = tab == StaticTab.All
    val showShowrooms: Boolean = tab == StaticTab.All
    val showBuy: Boolean = tab == StaticTab.All || tab == StaticTab.Buy
    val showSell: Boolean = tab == StaticTab.All || tab == StaticTab.Sell
    val showLoans: Boolean = tab == StaticTab.All || tab == StaticTab.Loans
    val showCarCheck: Boolean = tab == StaticTab.All || tab == StaticTab.Buy
    val showUsedCars: Boolean = tab == StaticTab.All || tab == StaticTab.Buy
    val showFinance: Boolean = tab == StaticTab.All || tab == StaticTab.Loans
}

sealed interface StaticHomeIntent {
    data class SelectTab(val tab: StaticTab) : StaticHomeIntent
    data class SelectUsedFilter(val filter: StaticUsedFilter) : StaticHomeIntent
    data class SelectTenure(val months: Int) : StaticHomeIntent
    data object OpenLoanSheet : StaticHomeIntent
    data object OpenAddVehicleSheet : StaticHomeIntent
    data object DismissSheet : StaticHomeIntent
    data class Navigate(val url: String) : StaticHomeIntent
}

sealed interface StaticHomeEffect {
    data class ShowNavigation(val url: String) : StaticHomeEffect
}

data class StaticHomeReduceResult(
    val state: StaticHomeUiState,
    val effect: StaticHomeEffect? = null,
)

fun reduceStaticHome(
    state: StaticHomeUiState,
    intent: StaticHomeIntent,
): StaticHomeReduceResult {
    return when (intent) {
        is StaticHomeIntent.SelectTab ->
            StaticHomeReduceResult(state.copy(tab = intent.tab))
        is StaticHomeIntent.SelectUsedFilter ->
            StaticHomeReduceResult(state.copy(usedCarFilter = intent.filter))
        is StaticHomeIntent.SelectTenure ->
            StaticHomeReduceResult(state.copy(tenureMonths = intent.months))
        StaticHomeIntent.OpenLoanSheet ->
            StaticHomeReduceResult(state.copy(sheetOpen = true, addVehicleSheetOpen = false))
        StaticHomeIntent.OpenAddVehicleSheet ->
            StaticHomeReduceResult(state.copy(addVehicleSheetOpen = true, sheetOpen = false))
        StaticHomeIntent.DismissSheet ->
            StaticHomeReduceResult(state.copy(sheetOpen = false, addVehicleSheetOpen = false))
        is StaticHomeIntent.Navigate ->
            StaticHomeReduceResult(state, StaticHomeEffect.ShowNavigation(intent.url))
    }
}
