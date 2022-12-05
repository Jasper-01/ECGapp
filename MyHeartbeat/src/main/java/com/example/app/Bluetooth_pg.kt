@file:Suppress("DEPRECATION")

package com.example.app

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Bluetooth_pg : AppCompatActivity() {
    private lateinit var onOffButton : Button
    private lateinit var status : TextView
    private lateinit var onOffImage : ImageView
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val REQUEST_CODE_ENABLE_BT : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_pg)
        Log.d("MyHeartBeat", "Bluetooth page create")

        val backbtn = findViewById<Button>(R.id.backbtn)
        backbtn.setOnClickListener {
            finish()
        }

        /*Bluetooth Adapter and Manager declaration */
        val askBTpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        /*layout xml details*/
        onOffButton = findViewById(R.id.OnOffButton)
        status = findViewById(R.id.BluetoothStatus)
        onOffImage = findViewById(R.id.OnOff_Image)
        val deviceName = findViewById<TextView>(R.id.DeviceName)
        val deviceAddress = findViewById<TextView>(R.id.deviceAddress)
        val showDevices = findViewById<Button>(R.id.listDevices)

        /* only shows Toast text once */
        if(bluetoothAdapter == null){
            Toast.makeText(applicationContext, "Bluetooth is not available on this device", Toast.LENGTH_SHORT).show()
            statusUnavailable()
        }

        /* Bluetooth check every 3 seconds
        * status check instead of re-creating the page over*/
        val bluetoothCheck = Handler()
        val bluetoothCheckRun = object : Runnable{
            override fun run() {
                /* check if BLUETOOTH availability*/
                when(bluetoothAdapter.isEnabled) {
                    true -> {  // can be either on or connected
                        statusOn()
                    }
                    false -> statusOff()
                    else -> statusUnavailable()
                }

                bluetoothCheck.postDelayed(this, 1000)
            }
        }
        bluetoothCheck.postDelayed(bluetoothCheckRun, 1000)

        /* check bluetooth permissions*/
        if (askBTpermission == PackageManager.PERMISSION_GRANTED) {
            Log.d("MyHeartBeat", "BLUETOOTH permission already granted")
        } else {
            Log.d("MyHeartBeat", "BLUETOOTH permission request")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), 99)
        }

        onOffButton.setOnClickListener {
            when(bluetoothAdapter.isEnabled){
                true -> {
                    bluetoothAdapter.disable()
                    Toast.makeText(applicationContext, "Turned off Bluetooth", Toast.LENGTH_SHORT).show()
                    statusOff()
                }
                false ->{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                        requestMultiplePermissions.launch(arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ))
                    }
                    val intentBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intentBT, REQUEST_CODE_ENABLE_BT)
                    if (callingActivity != null) {
                        Toast.makeText(
                            applicationContext,
                            "Turned on Bluetooth",
                            Toast.LENGTH_SHORT
                        ).show()
                        statusOn()
                    }
                }
                else -> {
                    Toast.makeText(applicationContext, "Bluetooth is not available on this device", Toast.LENGTH_SHORT).show()
                    statusUnavailable()
                }
            }
        }

        showDevices.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {

            } else if (!bluetoothAdapter.isEnabled) {
                Log.d("MyHeartBeat", "List Bluetooth devices failed : Bluetooth is disabled")
                Toast.makeText(applicationContext, "Please enable Bluetooth first", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MyHeartBeat", "List Bluetooth devices failed : Bluetooth unavailable")
                Toast.makeText(applicationContext, "Bluetooth is unavailable on this device", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            99 -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyHeartBeat", "BLUETOOTH permission granted")
                } else{
                    Toast.makeText(applicationContext, "App requires Bluetooth permissions", Toast.LENGTH_SHORT).show()
                    Log.d("MyHeartBeat", "BLUETOOTH permission not granted")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        when(requestCode){ REQUEST_CODE_ENABLE_BT ->
            if (requestCode == Activity.RESULT_OK){
                Log.d("MyHeartBeat", "Bluetooth enable approved")
            } else{
                Log.d("MyHeartBeat", "BLUETOOTH enable denied")
            }
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("MyHeartBeat", "${it.key} = ${it.value}")
            }
        }

    private fun statusOn(){
        Log.d("MyHeartBeat", "BLUETOOTH status on")
        status.setText(R.string.bluetooth_status_on)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_on)
        onOffButton.setText(R.string.turn_off)
    }

    private fun statusOff(){
        Log.d("MyHeartBeat", "BLUETOOTH status off")
        status.setText(R.string.bluetooth_status_off)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
        onOffButton.setText(R.string.turn_on)
    }

    private fun statusUnavailable(){
        Log.d("MyHeartBeat", "BLUETOOTH status unavailable")
        status.setText(R.string.bluetooth_not_supported)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
        onOffButton.setText(R.string.turn_on)
    }

    private fun statusConnected(){
        Log.d("MyHeartBeat", "BLUETOOTH status connected")
        status.setText(R.string.bluetooth_status_connected)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_connected)
        onOffButton.setText(R.string.turn_off)
    }

//    fun showPopup(v: View){
//        val popup = PopupMenu(this, v)
//        val inflater: MenuInflater = popup.menuInflater
//        inflater.inflate(R.menu.actions, popup.menu)
//        popup.show()
//    }
}