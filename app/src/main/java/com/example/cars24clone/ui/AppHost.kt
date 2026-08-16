package com.example.cars24clone.ui

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cars24clone.sdui.asset.SduiPayload
import com.example.cars24clone.sdui.mvi.SduiEffect
import com.example.cars24clone.sdui.mvi.SduiIntent
import com.example.cars24clone.sdui.mvi.SduiViewModel
import com.example.cars24clone.sdui.registry.defaultSduiRegistry
import com.example.cars24clone.sdui.render.SduiScreen
import com.example.cars24clone.ui.home.StaticHomeEffect
import com.example.cars24clone.ui.home.StaticHomeScreen
import com.example.cars24clone.ui.home.StaticHomeViewModel

enum class AppDestination(val label: String) {
    SduiHome("SDUI · Home"),
    SduiUnknown("SDUI · Unknown type"),
    SduiCarDetail("SDUI · Car detail"),
    StaticHome("Static · Home"),
}

@Composable
fun AppHost() {
    var destination by rememberSaveable { mutableStateOf(AppDestination.SduiHome) }
    val snackbar = remember { SnackbarHostState() }
    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbar) },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (destination) {
                AppDestination.StaticHome -> StaticHomePane(
                    onNavigate = { snackbar.showSnackbar(it) },
                )
                AppDestination.SduiHome -> SduiPane(
                    payload = SduiPayload.Home,
                    onNavigate = { snackbar.showSnackbar(it) },
                )
                AppDestination.SduiUnknown -> SduiPane(
                    payload = SduiPayload.UnknownType,
                    onNavigate = { snackbar.showSnackbar(it) },
                )
                AppDestination.SduiCarDetail -> SduiPane(
                    payload = SduiPayload.CarDetail,
                    onNavigate = { snackbar.showSnackbar(it) },
                )
            }
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(4.dp),
            ) {
                IconButton(onClick = { menuOpen = true }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "Switch screen",
                        tint = if (destination == AppDestination.SduiUnknown) {
                            Color.Black
                        } else {
                            Color.White
                        },
                    )
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    AppDestination.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.label) },
                            onClick = {
                                destination = item
                                menuOpen = false
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SduiPane(
    payload: SduiPayload,
    onNavigate: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: SduiViewModel = viewModel(
        key = payload.path,
        factory = SduiViewModel.factory(application),
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val registry = remember { defaultSduiRegistry() }

    LaunchedEffect(payload) {
        viewModel.onIntent(SduiIntent.SelectPayload(payload))
    }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is SduiEffect.ShowNavigation -> onNavigate(effect.url)
            }
        }
    }

    val document = uiState.document
    when {
        uiState.loadError != null -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.loadError ?: "")
            }
        }
        document != null -> {
            SduiScreen(
                document = document,
                nodeState = uiState.nodeState,
                openSheetId = uiState.openSheetId,
                onIntent = viewModel::onIntent,
                registry = registry,
                modifier = modifier,
            )
        }
        else -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun StaticHomePane(
    onNavigate: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: StaticHomeViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is StaticHomeEffect.ShowNavigation -> onNavigate(effect.url)
            }
        }
    }
    StaticHomeScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}
