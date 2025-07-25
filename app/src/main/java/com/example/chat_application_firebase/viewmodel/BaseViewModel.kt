package com.example.chat_application_firebase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<STATE, SIDE_EFFECT> : ViewModel() {

    abstract fun setDefaultState(): STATE

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(setDefaultState())
    val state: StateFlow<STATE> = _state

    private val _sideEffect: MutableSharedFlow<SIDE_EFFECT> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val sideEffect: SharedFlow<SIDE_EFFECT> = _sideEffect
    protected fun updateState(oldState: (STATE) -> STATE) = viewModelScope.launch {
        _state.emit(oldState(_state.value))
    }

    fun postSideEffect(sideEffect: SIDE_EFFECT) = viewModelScope.launch {
        _sideEffect.emit(sideEffect)
    }
}