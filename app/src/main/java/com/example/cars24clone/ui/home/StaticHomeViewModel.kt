package com.example.cars24clone.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class StaticHomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(StaticHomeUiState())
    val state: StateFlow<StaticHomeUiState> = _state.asStateFlow()

    private val _effects = Channel<StaticHomeEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: StaticHomeIntent) {
        val result = reduceStaticHome(_state.value, intent)
        _state.value = result.state
        val effect = result.effect ?: return
        viewModelScope.launch { _effects.send(effect) }
    }
}
