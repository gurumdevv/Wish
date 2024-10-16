package com.gurumlab.wish.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject


class NetWorkManager @Inject constructor(
    private val context: Context
) {

    fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val currentNetwork = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return false

        val result = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
            else -> false
        }
        return result
    }
}