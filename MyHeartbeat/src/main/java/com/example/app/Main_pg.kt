package com.example.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Main_pg : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MyHeartBeat", "Homepage create")
        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val display_name = intent.getStringExtra("full_name")
        Log.d("DKMOBILE", display_name.toString())

        val sharedPref = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("email", email.toString())
        editor.putString("full_name", display_name.toString())
        editor.apply()

        val sign_out = findViewById<Button>(R.id.sign_out)
        val heartRate = findViewById<Button>(R.id.HeartRate)
        val bluetooth = findViewById<Button>(R.id.BT)
        val friends = findViewById<Button>(R.id.History)
        val settings = findViewById<Button>(R.id.UserInfo)
        val credits = findViewById<Button>(R.id.Credits)

        sign_out.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, login_google_pg::class.java))
        }
        heartRate.setOnClickListener {
            val Intent = Intent(this, HeartRate_pg::class.java)
            startActivity(Intent)
        }

        bluetooth.setOnClickListener {
            val Intent = Intent(this, Bluetooth_pg::class.java)
            startActivity(Intent)
        }

        friends.setOnClickListener {
            val Intent = Intent(this, History_pg::class.java)
            startActivity(Intent)
        }

        settings.setOnClickListener {
            val Intent = Intent(this, UserInfo_pg::class.java)
            startActivity(Intent)
        }

        credits.setOnClickListener {
            val Intent = Intent(this, Credits_pg::class.java)
            startActivity(Intent)
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            Log.d("MyHeartBeat", "Homepage BACK twice -> exit")
            finishAffinity()
        }
        else {
            Log.d("MyHeartBeat", "Homepage BACK once")
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(applicationContext, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}