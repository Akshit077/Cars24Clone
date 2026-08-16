package com.example.cars24clone.sdui.render

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.cars24clone.perf.PerfTrace
import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.mvi.SduiIntent
import com.example.cars24clone.sdui.registry.SduiRegistry
import kotlinx.serialization.json.JsonObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SduiScreen(
    document: SduiDocument,
    nodeState: JsonObject,
    openSheetId: String?,
    onIntent: (SduiIntent) -> Unit,
    registry: SduiRegistry,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    LaunchedEffect(document.screen.id) {
        PerfTrace.markFullyDrawn("sdui_fully_drawn", context)
    }
    val scope = SduiRenderScope(
        document = document,
        nodeState = nodeState,
        registry = registry,
        onIntent = onIntent,
    )
    Box(
        modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        SduiNodeView(
            node = document.root,
            scope = scope,
            modifier = Modifier.fillMaxSize(),
            scrollable = document.root.type == "column" || document.root.type == "list",
        )
        if (openSheetId != null) {
            val sheetNode = document.sheets[openSheetId]
            ModalBottomSheet(
                onDismissRequest = { onIntent(SduiIntent.DismissSheet) },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            ) {
                if (sheetNode != null) {
                    SduiNodeView(sheetNode, scope)
                } else {
                    Text("Unknown sheet: $openSheetId")
                }
            }
        }
    }
}
