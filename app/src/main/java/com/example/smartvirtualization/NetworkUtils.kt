package com.example.smartvirtualization.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Utility class for network connectivity checks.
 * Example usage:
 * ```
 * val networkUtils = NetworkUtils(context)
 * if (networkUtils.isConnected()) {
 *     // Perform network operations
 * }
 * ```
 */
internal class NetworkUtils(private val context: Context) {
    /**
     * Checks if there is an active internet connection.
     * @return true if internet is available, false otherwise
     */
    internal fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}