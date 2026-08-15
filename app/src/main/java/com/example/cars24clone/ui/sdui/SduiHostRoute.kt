package com.example.cars24clone.ui.sdui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cars24clone.sdui.asset.SduiPayload
import com.example.cars24clone.sdui.asset.loadSduiDocument
import com.example.cars24clone.sdui.registry.defaultSduiRegistry
import com.example.cars24clone.sdui.render.SduiScreen
import com.example.cars24clone.sdui.runtime.SduiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SduiHostRoute() {
    val context = LocalContext.current
    var payload by remember { mutableStateOf(SduiPayload.Home) }
    val document = remember(payload) { loadSduiDocument(context, payload.path) }
    val controller = remember(document) { SduiController(document) }
    val registry = remember { defaultSduiRegistry() }
    val snackbar = remember { SnackbarHostState() }
    var menuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(controller.navigationUrl) {
        val url = controller.navigationUrl ?: return@LaunchedEffect
        snackbar.showSnackbar(url)
        controller.consumeNavigation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(document.screen.title.ifEmpty { payload.label }) },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Switch payload")
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        SduiPayload.entries.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.label) },
                                onClick = {
                                    payload = item
                                    menuOpen = false
                                },
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { innerPadding ->
        SduiScreen(
            document = document,
            controller = controller,
            registry = registry,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
