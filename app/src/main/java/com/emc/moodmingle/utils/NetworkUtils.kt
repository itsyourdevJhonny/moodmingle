package com.emc.moodmingle.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.net.SocketFactory

class NetworkUtils(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observeNetworkChanges(): Flow<NetworkStatus> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(checkNetworkStatus(network))
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.NoInternet)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(checkNetworkStatus(network))
            }
        }

        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            networkCallback
        )

        trySend(checkNetworkStatus())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    private fun checkNetworkStatus(network: Network? = null): NetworkStatus {
        val hasInternetAccess = hasInternetAccess(network)
        if (!hasInternetAccess) return NetworkStatus.NoInternet

        val isConnectionFast = isConnectionFast(network)
        return if (!isConnectionFast) NetworkStatus.SlowInternet else NetworkStatus.Connected
    }

    private fun hasInternetAccess(network: Network? = null): Boolean {
        val activeNetwork = network ?: connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun isConnectionFast(network: Network? = null): Boolean {
        val client = OkHttpClient.Builder()
            .socketFactory(network?.socketFactory ?: SocketFactory.getDefault())
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(3, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("http://connectivitycheck.gstatic.com/generate_204")
            .build()

        return try {
            val start = System.currentTimeMillis()
            val response = client.newCall(request).execute()
            val latency = System.currentTimeMillis() - start
            response.close()
            response.isSuccessful && latency < 1000
        } catch (_: Exception) {
            false
        }
    }

    fun isInternetAvailable(): Boolean {
        return hasInternetAccess()
    }
}

sealed class NetworkStatus {
    object Connected : NetworkStatus()
    object SlowInternet : NetworkStatus()
    object NoInternet : NetworkStatus()
}
