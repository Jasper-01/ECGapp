package com.example.app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Main_pg : ThemeChange() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var auth: FirebaseAuth

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_main)
        Log.d("MyHeartBeat", "Homepage create")
        auth = FirebaseAuth.getInstance()

        val heartRate = findViewById<Button>(R.id.HeartRate)
        val bluetooth = findViewById<Button>(R.id.BT)
        val friends = findViewById<Button>(R.id.History)
        val settings = findViewById<Button>(R.id.UserInfo)
        val credits = findViewById<Button>(R.id.Credits)

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        BluetoothObj.requestBTPermissions(this)

        val backBtn:View = findViewById<Button>(R.id.backbtn)
        backBtn.setOnClickListener {
            finishAffinity()
        }

        val TChanger: View = findViewById<Button>(R.id.themeChanger)
        TChanger.setOnClickListener {
            switchTheme()
            recreate()
        }

        heartRate.setOnClickListener {
            if(BluetoothObj.checkBTpermissions(this)) {
                val Intent = Intent(this, HeartRate_pg::class.java)
                startActivity(Intent)
            } else {
                Toast.makeText(applicationContext, "Please allow bluetooth permissions", Toast.LENGTH_SHORT).show()
                BluetoothObj.requestBTPermissions(this)
            }
        }

        bluetooth.setOnClickListener {
            if(BluetoothObj.checkBTpermissions(this)) {
                val Intent = Intent(this, Bluetooth_pg::class.java)
                startActivity(Intent)
            } else {
                Toast.makeText(applicationContext, "Please allow bluetooth permissions", Toast.LENGTH_SHORT).show()
                BluetoothObj.requestBTPermissions(this)
            }
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