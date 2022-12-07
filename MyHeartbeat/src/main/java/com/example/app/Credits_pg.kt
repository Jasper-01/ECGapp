package com.example.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Credits_pg : ThemeChange() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_credits_pg)
        Log.d("MyHeartBeat", "Credits page create")

        val backBtn: View = findViewById<Button>(R.id.backbtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}