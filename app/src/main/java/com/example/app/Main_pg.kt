package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Main_pg : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heartRate = findViewById<Button>(R.id.HeartRate)
        val bluetooth = findViewById<Button>(R.id.BT)
        val friends = findViewById<Button>(R.id.FL)
        val settings = findViewById<Button>(R.id.Settings)
        val credits = findViewById<Button>(R.id.Credits)

        heartRate.setOnClickListener {
            val Intent = Intent(this, HeartRate_pg::class.java)
            startActivity(Intent)
        }

        bluetooth.setOnClickListener {
            val Intent = Intent(this, Bluetooth_pg::class.java)
            startActivity(Intent)
        }

        friends.setOnClickListener {
            val Intent = Intent(this, FriendsList_pg::class.java)
            startActivity(Intent)
        }

        settings.setOnClickListener {
            val Intent = Intent(this, Settings_pg::class.java)
            startActivity(Intent)
        }

        credits.setOnClickListener {
            val Intent = Intent(this, Credits::class.java)
            startActivity(Intent)
        }
    }
}