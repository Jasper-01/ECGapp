package com.example.app

import android.os.Bundle
import android.widget.TextClock
import androidx.appcompat.app.AppCompatActivity

class HeartRate_pg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate_pg)
        val textClock = findViewById<TextClock>(R.id.Time)

        textClock.format12Hour = null
        textClock.format24Hour = "yyyy, LLLL dd (E) HH:mm:ss"
    }
}