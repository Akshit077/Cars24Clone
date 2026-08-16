package com.example.cars24clone

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.example.cars24clone.perf.PerfTrace
import okhttp3.OkHttpClient

class Cars24App : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        PerfTrace.mark("application_onCreate")
    }

    override fun newImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(OkHttpClient()))
            }
            .crossfade(true)
            .build()
    }
}
