package com.example.cars24clone.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cars24clone.perf.PerfTrace
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.cars24clone.ui.components.AppActionButton
import com.example.cars24clone.ui.components.AppButtonVariant
import com.example.cars24clone.ui.components.AppChipVariant
import com.example.cars24clone.ui.components.AppFilterChip
import com.example.cars24clone.ui.components.AppNetworkImage
import com.example.cars24clone.ui.components.AppSearchBar
import com.example.cars24clone.ui.components.AppSearchVariant
import com.example.cars24clone.ui.components.AppSectionHeader
import com.example.cars24clone.ui.components.AppText
import com.example.cars24clone.ui.components.AppTextVariant
import com.example.cars24clone.ui.theme.CarsBuyCard
import com.example.cars24clone.ui.theme.CarsCream
import com.example.cars24clone.ui.theme.CarsHero
import com.example.cars24clone.ui.theme.CarsOrbit
import com.example.cars24clone.ui.theme.CarsSellCard

/**
 * Hardcoded Compose home that mirrors `home.json`.
 * Exists so [PERF.md] can compare engine overhead against the same leaves
 * (`AppText`, `AppSearchBar`, `AppFilterChip`, `AppNetworkImage`, `AppActionButton`).
 * Not the product path — that is SDUI · Home.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticHomeScreen(
    state: StaticHomeUiState,
    onIntent: (StaticHomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        PerfTrace.recordParse(0)
        PerfTrace.markFullyDrawn("static_fully_drawn", context)
    }
    Box(
        modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            HeroBlock(state, onIntent)
            if (state.showOrbit) OrbitBanner(onIntent)
            if (state.showShowrooms) ShowroomRail(onIntent)
            if (state.showBuy) BuyRail(onIntent)
            if (state.showSell) SellRail(onIntent)
            if (state.showLoans) LoanRail(onIntent)
            if (state.showCarCheck) CarCheckGrid(onIntent)
            if (state.showUsedCars) UsedCarRail(state, onIntent)
            if (state.showFinance) FinanceCard(state, onIntent)
            Spacer(Modifier.height(24.dp))
        }
        if (state.sheetOpen) {
            LoanSheet(state.emi, onIntent)
        }
        if (state.addVehicleSheetOpen) {
            AddVehicleSheet(onIntent)
        }
    }
}

@Composable
private fun HeroBlock(
    state: StaticHomeUiState,
    onIntent: (StaticHomeIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CarsHero)
            .statusBarsPadding()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        AppSearchBar(
            placeholder = "Search Swift",
            onClick = { onIntent(StaticHomeIntent.Navigate("sdui://search")) },
            modifier = Modifier.padding(horizontal = 16.dp),
            variant = AppSearchVariant.Hero,
        )
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            StaticTab.entries.forEach { tab ->
                AppFilterChip(
                    text = tab.label,
                    selected = state.tab == tab,
                    onClick = { onIntent(StaticHomeIntent.SelectTab(tab)) },
                    variant = AppChipVariant.Tab,
                )
            }
        }
        if (state.showManage) {
            AppSectionHeader(
                title = "Manage your vehicle",
                titleVariant = AppTextVariant.OnHeroTitle,
                actionText = "+ Add vehicle",
                onAction = { onIntent(StaticHomeIntent.OpenAddVehicleSheet) },
            )
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StaticHomeCatalog.manageTiles.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEach { tile ->
                            ServiceTile(
                                tile = tile,
                                color = MaterialTheme.colorScheme.surface,
                                onClick = { onIntent(StaticHomeIntent.Navigate(tile.route)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.ServiceTile(
    tile: StaticServiceTile,
    color: Color,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(Modifier.padding(8.dp)) {
            AppText(tile.title, AppTextVariant.Body, minLines = 2, maxLines = 2)
            AppNetworkImage(tile.imageUrl, 1f, contentScale = ContentScale.Fit)
        }
    }
}

@Composable
private fun OrbitBanner(onIntent: (StaticHomeIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(title = "Offers for you")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = CarsOrbit),
            elevation = CardDefaults.cardElevation(0.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AppText("Cars24 x Spotify Premium", AppTextVariant.OnHero)
                AppText("Add your car to Orbit", AppTextVariant.OnHeroTitle)
                AppText("Enjoy 3-months Spotify Premium free", AppTextVariant.OnHero)
                AppActionButton(
                    text = "Add car now",
                    variant = AppButtonVariant.Inverse,
                    onClick = { onIntent(StaticHomeIntent.OpenAddVehicleSheet) },
                )
                AppNetworkImage(
                    StaticHomeCatalog.orbitImage,
                    16f / 9f,
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
private fun ShowroomRail(onIntent: (StaticHomeIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(title = "3 showrooms in your city")
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(StaticHomeCatalog.showrooms, key = { it.route }) { showroom ->
                Card(
                    onClick = { onIntent(StaticHomeIntent.Navigate(showroom.route)) },
                    modifier = Modifier.width(220.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    AppNetworkImage(showroom.imageUrl, 4f / 3f)
                    Column(
                        Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        AppText(showroom.inventory, AppTextVariant.Caption)
                        AppText(showroom.name, AppTextVariant.Title)
                    }
                }
            }
        }
    }
}

@Composable
private fun BuyRail(onIntent: (StaticHomeIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(
            title = "Buy car",
            actionText = "Up to ₹80,000 off",
            actionVariant = "badge",
        )
        PromoRail(StaticHomeCatalog.buyCards, CarsBuyCard, onIntent)
    }
}

@Composable
private fun SellRail(onIntent: (StaticHomeIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(title = "Sell your car")
        PromoRail(StaticHomeCatalog.sellCards, CarsSellCard, onIntent)
    }
}

@Composable
private fun PromoRail(
    cards: List<StaticPromoCard>,
    color: Color,
    onIntent: (StaticHomeIntent) -> Unit,
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(cards, key = { it.route }) { card ->
            Card(
                onClick = { onIntent(StaticHomeIntent.Navigate(card.route)) },
                modifier = Modifier.width(148.dp),
                colors = CardDefaults.cardColors(containerColor = color),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(Modifier.padding(8.dp)) {
                    AppText(card.title, AppTextVariant.OnHero)
                    AppNetworkImage(card.imageUrl, 4f / 3f)
                }
            }
        }
    }
}

@Composable
private fun LoanRail(onIntent: (StaticHomeIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(title = "Get loans")
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(StaticHomeCatalog.loans, key = { it.route }) { item ->
                Column(
                    modifier = Modifier.width(96.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Card(
                        onClick = { onIntent(StaticHomeIntent.Navigate(item.route)) },
                        colors = CardDefaults.cardColors(containerColor = CarsCream),
                        elevation = CardDefaults.cardElevation(0.dp),
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            AppNetworkImage(item.imageUrl, 1f)
                        }
                    }
                    AppText(item.title, AppTextVariant.Caption)
                }
            }
        }
    }
}

@Composable
private fun CarCheckGrid(onIntent: (StaticHomeIntent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(title = "Car check services")
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StaticHomeCatalog.carChecks.chunked(3).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    row.forEach { tile ->
                        ServiceTile(
                            tile = tile,
                            color = CarsCream,
                            onClick = { onIntent(StaticHomeIntent.Navigate(tile.route)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UsedCarRail(
    state: StaticHomeUiState,
    onIntent: (StaticHomeIntent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppSectionHeader(
            title = "Used cars you'll love",
            actionText = "View all",
            onAction = { onIntent(StaticHomeIntent.Navigate("sdui://buy/all")) },
        )
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StaticUsedFilter.entries.forEach { filter ->
                AppFilterChip(
                    text = filter.label,
                    selected = state.usedCarFilter == filter,
                    onClick = { onIntent(StaticHomeIntent.SelectUsedFilter(filter)) },
                )
            }
        }
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(state.cars, key = { it.id }) { car ->
                Card(
                    onClick = { onIntent(StaticHomeIntent.Navigate(car.route)) },
                    modifier = Modifier.width(240.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(0.dp),
                ) {
                    AppNetworkImage(car.imageUrl, 4f / 3f)
                    Column(
                        Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        car.badge?.let { AppText(it, AppTextVariant.Caption) }
                        AppText(car.title, AppTextVariant.Title)
                        AppText(car.meta, AppTextVariant.Caption)
                        AppText(car.price, AppTextVariant.Price)
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceCard(
    state: StaticHomeUiState,
    onIntent: (StaticHomeIntent) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppText("Car loan EMI", AppTextVariant.Title)
            AppText(
                "For a ₹4.85 Lakh Swift. Change tenure — EMI updates from a hardcoded map in this static twin.",
                AppTextVariant.Caption,
            )
            AppText(state.emi, AppTextVariant.Price)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(12, 24, 36).forEach { months ->
                    AppFilterChip(
                        text = "$months mo",
                        selected = state.tenureMonths == months,
                        onClick = { onIntent(StaticHomeIntent.SelectTenure(months)) },
                    )
                }
            }
            AppActionButton(
                text = "Check eligibility",
                variant = AppButtonVariant.Primary,
                onClick = { onIntent(StaticHomeIntent.OpenLoanSheet) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanSheet(emi: String, onIntent: (StaticHomeIntent) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onIntent(StaticHomeIntent.DismissSheet) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppText("Check loan eligibility", AppTextVariant.Title)
            AppText(
                "Share a few details. We will show offers from our partner lenders.",
                AppTextVariant.Body,
            )
            AppText(emi, AppTextVariant.Price)
            AppActionButton(
                text = "Continue",
                variant = AppButtonVariant.Primary,
                onClick = { onIntent(StaticHomeIntent.DismissSheet) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVehicleSheet(onIntent: (StaticHomeIntent) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onIntent(StaticHomeIntent.DismissSheet) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppText("Add your vehicle", AppTextVariant.Title)
            AppText(
                "Enter your registration number to add a car to Orbit and unlock challan, FASTag, and insurance.",
                AppTextVariant.Body,
            )
            AppActionButton(
                text = "Continue",
                variant = AppButtonVariant.Primary,
                onClick = { onIntent(StaticHomeIntent.DismissSheet) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
