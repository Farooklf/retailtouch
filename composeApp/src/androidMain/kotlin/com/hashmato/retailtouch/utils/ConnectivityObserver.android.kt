package com.hashmato.retailtouch.utils



import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual object ConnectivityObserver : KoinComponent {
    private val _isConnected = MutableStateFlow(false)
    actual val isConnected = _isConnected.asStateFlow()

    private val context: Context by inject() // Inject context dynamically

    actual fun startObserving() {
        context.let { ctx ->
            val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isConnected.update { true }
                    ToastManager.showToast("you are online ✅")
                }

                override fun onLost(network: Network) {
                    _isConnected.update { false }
                    ToastManager.showToast("Found No Internet Connection ❌")
                }
            }

            val networkRequest = android.net.NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, callback)
        }
    }
}
