package com.example.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class Credits_pg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits_pg)
        Log.d("MyHeartBeat", "Credits page create")
    }
}