package com.example.smartvirtualization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.smartvirtualization.activities.ClientActivity
import com.example.smartvirtualization.activities.HostActivity

class MainActivity : Activity() {
    private lateinit var hostButton: Button
    private lateinit var clientButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupButtons()
    }

    private fun setupButtons() {
        hostButton = findViewById(R.id.hostButton)
        clientButton = findViewById(R.id.clientButton)

        hostButton.setOnClickListener {
            val intent = Intent(this, HostActivity::class.java)
            startActivity(intent)
        }

        clientButton.setOnClickListener {
            val intent = Intent(this, ClientActivity::class.java)
            startActivity(intent)
        }
    }
}