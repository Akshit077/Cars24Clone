package com.example.cars24clone.perf

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.SystemClock
import android.util.Log

/**
 * Shared marks for [PERF.md]. Static parse is always 0.
 * Grep Logcat: `adb logcat -s Cars24Perf`
 */
object PerfTrace {
    const val TAG = "Cars24Perf"

    val processStartMs: Long = SystemClock.elapsedRealtime()

    @Volatile
    var parseMs: Long = 0
        private set

    fun recordParse(ms: Long) {
        parseMs = ms
        Log.i(TAG, "parse_ms=$ms")
    }

    fun mark(name: String) {
        val elapsed = SystemClock.elapsedRealtime() - processStartMs
        Log.i(TAG, "$name elapsed_ms=$elapsed parse_ms=$parseMs")
    }

    fun markFullyDrawn(name: String, context: Context) {
        mark(name)
        context.findActivity()?.reportFullyDrawn()
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
