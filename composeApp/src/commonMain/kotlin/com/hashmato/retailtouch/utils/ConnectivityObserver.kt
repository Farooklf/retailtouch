package com.hashmato.retailtouch.utils

import kotlinx.coroutines.flow.StateFlow

expect  object ConnectivityObserver {
    val isConnected: StateFlow<Boolean>
    fun startObserving() // Call this function at the start of your app
}