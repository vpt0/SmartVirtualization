package com.example.smartvirtualization.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartvirtualization.R
import com.example.smartvirtualization.databinding.ActivityClientBinding
import com.example.smartvirtualization.utils.WebSocketManager

class ClientActivity : AppCompatActivity() {
    private var _binding: ActivityClientBinding? = null
    private val binding get() = _binding!!
    private val webSocketManager = WebSocketManager.getInstance()
    private var isConnected = false

    companion object {
        private const val TAG = "ClientActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            connectButton.setOnClickListener {
                if (!isConnected) {
                    connectToHost()
                } else {
                    disconnectFromHost()
                }
            }
        }
    }

    private fun connectToHost() {
        val ipAddress = binding.ipAddressInput.text?.toString()?.trim() ?: ""
        val sessionId = binding.sessionIdInput.text?.toString()?.trim() ?: ""

        if (ipAddress.isEmpty() || sessionId.isEmpty()) {
            showError(getString(R.string.error_empty_fields))
            return
        }

        val serverUrl = "ws://$ipAddress:8080"

        try {
            webSocketManager.startConnection(
                serverUrl = serverUrl,
                onConnected = {
                    runOnUiThread {
                        connectionEstablished()
                    }
                },
                onMessageReceived = { message ->
                    handleIncomingMessage(message)
                },
                onFailure = { error ->
                    runOnUiThread {
                        showError(getString(R.string.error_connection_failed, error))
                        resetConnection()
                    }
                }
            )

            binding.apply {
                statusText.text = getString(R.string.status_connecting)
                connectButton.isEnabled = false
            }

        } catch (e: Exception) {
            showError(getString(R.string.error_connection, e.message))
        }
    }

    private fun handleIncomingMessage(message: String) {
        try {
            val imageBytes = Base64.decode(message, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            runOnUiThread {
                updateScreenView(bitmap)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing message: ${e.message}")
        }
    }

    private fun updateScreenView(bitmap: Bitmap) {
        binding.apply {
            screenView.setImageBitmap(bitmap)
            if (screenView.visibility != View.VISIBLE) {
                screenView.visibility = View.VISIBLE
            }
        }
    }

    private fun connectionEstablished() {
        isConnected = true
        binding.apply {
            connectButton.isEnabled = true
            connectButton.text = getString(R.string.disconnect)
            statusText.text = getString(R.string.status_connected)
            statusText.setTextColor(Color.GREEN)
            ipAddressInput.isEnabled = false
            sessionIdInput.isEnabled = false
        }
    }

    private fun disconnectFromHost() {
        webSocketManager.closeConnection()
        resetConnection()
    }

    private fun resetConnection() {
        isConnected = false
        binding.apply {
            connectButton.isEnabled = true
            connectButton.text = getString(R.string.connect)
            statusText.text = ""
            screenView.visibility = View.GONE
            ipAddressInput.isEnabled = true
            sessionIdInput.isEnabled = true
        }
    }

    private fun showError(message: String) {
        binding.apply {
            statusText.text = message
            statusText.setTextColor(Color.RED)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.closeConnection()
        _binding = null
    }
}