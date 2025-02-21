package com.hashmato.retailtouch.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

actual object ConnectivityObserver {
    private val _isConnected = MutableStateFlow(false)
    actual val isConnected = _isConnected.asStateFlow()

    private val monitor = nw_path_monitor_create()
    private val queue = dispatch_queue_create("NetworkMonitorQueue", null)

    actual fun startObserving() {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val newStatus = nw_path_get_status(path) == nw_path_status_satisfied
            if (_isConnected.value != newStatus) {
                _isConnected.value = newStatus
                if (newStatus) {
                    ToastManager.showToast("Internet Connected ✅")
                } else {
                    ToastManager.showToast("No Internet Connection ❌")
                }
            }
        }
        nw_path_monitor_start(monitor, queue)
    }
}