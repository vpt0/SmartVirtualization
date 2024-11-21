package com.example.smartvirtualization.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class WebSocketManager private constructor() {
    private var connection: HttpURLConnection? = null
    private var isConnecting = false

    companion object {
        private const val TAG = "WebSocketManager"
        private var instance: WebSocketManager? = null

        @Synchronized
        fun getInstance(): WebSocketManager {
            return instance ?: WebSocketManager().also { instance = it }
        }
    }

    suspend fun startConnection(
        serverUrl: String,
        onConnected: () -> Unit,
        onMessageReceived: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (isConnecting) {
            Log.d(TAG, "Connection attempt already in progress")
            return
        }

        try {
            isConnecting = true
            withContext(Dispatchers.IO) {
                val url = URL(serverUrl)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    doInput = true
                    doOutput = true
                }

                connection?.let { conn ->
                    if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                        onConnected()
                        val reader = BufferedReader(InputStreamReader(conn.inputStream))
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            line?.let { onMessageReceived(it) }
                        }
                    } else {
                        onFailure("Connection failed: ${conn.responseMessage}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection error", e)
            onFailure("Connection error: ${e.message}")
        } finally {
            isConnecting = false
        }
    }

    suspend fun sendMessage(message: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                connection?.let { conn ->
                    OutputStreamWriter(conn.outputStream).use { writer ->
                        writer.write(message)
                        writer.flush()
                    }
                    true
                } ?: false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message", e)
            false
        }
    }

    fun closeConnection() {
        try {
            connection?.disconnect()
            connection = null
            isConnecting = false
        } catch (e: Exception) {
            Log.e(TAG, "Error closing connection", e)
        }
    }
}