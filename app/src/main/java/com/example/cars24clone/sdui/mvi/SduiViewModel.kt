package com.example.cars24clone.sdui.mvi

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cars24clone.sdui.asset.SduiPayload
import com.example.cars24clone.sdui.asset.loadSduiDocument
import com.example.cars24clone.sdui.model.SduiDocument
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SduiViewModel(
    private val loadDocument: (SduiPayload) -> SduiDocument,
) : ViewModel() {

    private val _state = MutableStateFlow(SduiUiState())
    val state: StateFlow<SduiUiState> = _state.asStateFlow()

    private val _effects = Channel<SduiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: SduiIntent) {
        when (intent) {
            is SduiIntent.SelectPayload -> loadPayload(intent)
            else -> apply(intent)
        }
    }

    private fun loadPayload(intent: SduiIntent.SelectPayload) {
        apply(intent)
        runCatching { loadDocument(intent.payload) }
            .onSuccess { document -> apply(SduiIntent.DocumentLoaded(intent.payload, document)) }
            .onFailure { error ->
                apply(
                    SduiIntent.LoadFailed(
                        payload = intent.payload,
                        message = error.message ?: "Failed to load ${intent.payload.path}",
                    ),
                )
            }
    }

    private fun apply(intent: SduiIntent) {
        val result = reduce(_state.value, intent)
        _state.value = result.state
        val effect = result.effect ?: return
        viewModelScope.launch { _effects.send(effect) }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SduiViewModel { payload ->
                        loadSduiDocument(application, payload.path)
                    } as T
                }
            }
        }
    }
}
