package com.example.cars24clone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cars24clone.ui.sdui.SduiHostRoute
import com.example.cars24clone.ui.theme.Cars24CloneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cars24CloneTheme {
                SduiHostRoute()
            }
        }
    }
}
