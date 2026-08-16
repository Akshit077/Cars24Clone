package com.example.cars24clone.sdui.asset

import android.content.Context
import android.os.SystemClock
import com.example.cars24clone.perf.PerfTrace
import com.example.cars24clone.sdui.model.SduiDocument
import com.example.cars24clone.sdui.model.SduiJson

enum class SduiPayload(val path: String, val label: String) {
    Home("sdui/home.json", "Home"),
    UnknownType("sdui/home_unknown_type.json", "Unknown type"),
    CarDetail("sdui/car_detail_sketch.json", "Car detail"),
}

fun loadSduiDocument(context: Context, path: String): SduiDocument {
    val json = context.assets.open(path).bufferedReader().use { it.readText() }
    val started = SystemClock.elapsedRealtime()
    val document = SduiJson.decodeFromString(SduiDocument.serializer(), json)
    PerfTrace.recordParse(SystemClock.elapsedRealtime() - started)
    return document
}
