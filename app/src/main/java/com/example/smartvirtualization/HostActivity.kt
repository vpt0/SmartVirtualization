package com.example.smartvirtualization.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.smartvirtualization.R

class HostActivity : Activity() {
    private lateinit var startButton: Button
    private lateinit var statusText: TextView
    private var isHosting = false
    private lateinit var mediaProjectionManager: MediaProjectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        setupViews()
        setupClickListeners()
    }

    private fun setupViews() {
        startButton = findViewById(R.id.startHostingButton)
        statusText = findViewById(R.id.statusText)
    }

    private fun setupClickListeners() {
        startButton.setOnClickListener {
            if (!isHosting) {
                startHosting()
            } else {
                stopHosting()
            }
        }
    }

    private fun startHosting() {
        try {
            val intent = mediaProjectionManager.createScreenCaptureIntent()
            startActivityForResult(intent, SCREEN_CAPTURE_REQUEST_CODE)
        } catch (e: Exception) {
            showToast("Failed to start hosting: ${e.message}")
        }
    }

    private fun stopHosting() {
        isHosting = false
        updateUI()
    }

    private fun updateUI() {
        startButton.text = if (isHosting) "Stop Hosting" else "Start Hosting"
        statusText.text = if (isHosting) "Status: Hosting" else "Status: Stopped"
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCREEN_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            isHosting = true
            updateUI()
        } else {
            showToast("Screen sharing permission denied")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val SCREEN_CAPTURE_REQUEST_CODE = 100
    }
}