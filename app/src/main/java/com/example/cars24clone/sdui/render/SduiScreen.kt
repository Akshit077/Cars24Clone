package com.example.cars24clone.sdui.render

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.registry.SduiRegistry
import com.example.cars24clone.sdui.runtime.SduiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SduiScreen(
    document: SduiDocument,
    controller: SduiController,
    registry: SduiRegistry,
    modifier: Modifier = Modifier,
) {
    val scope = SduiRenderScope(document, controller, registry)
    Box(modifier.fillMaxSize()) {
        SduiNodeView(
            node = document.root,
            scope = scope,
            modifier = Modifier.fillMaxSize(),
            scrollable = document.root.type == "column" || document.root.type == "list",
        )
        val sheetId = controller.openSheetId
        if (sheetId != null) {
            val sheetNode = document.sheets[sheetId]
            ModalBottomSheet(
                onDismissRequest = controller::dismissSheet,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            ) {
                if (sheetNode != null) {
                    SduiNodeView(sheetNode, scope)
                } else {
                    Text("Unknown sheet: $sheetId")
                }
            }
        }
    }
}
