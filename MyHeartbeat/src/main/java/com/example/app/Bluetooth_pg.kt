@file:Suppress("DEPRECATION")

package com.example.app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Bluetooth_pg : ThemeChange() {
    private lateinit var onOffButton: Button
    private lateinit var status: TextView
    private lateinit var onOffImage: ImageView
    private lateinit var showDevicesNearby: Button
    private lateinit var listPairedDevices: ListView
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    var mDeviceList = ArrayList<String>()
    var devices = 0

    private val REQUEST_CODE_ENABLE_BT: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_bluetooth_pg)
        Log.d("MyHeartBeat", "Bluetooth page create")

        /* check bluetooth permissions*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("MyHeartBeat", "Bluetooth permission request.")
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    2
                )
                return
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED){
                Log.d("MyHeartBeat", "BLUETOOTH permission already granted")
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                Log.d("MyHeartBeat", "BLUETOOTH permission already granted")
            } else {
                Log.d("MyHeartBeat", "Bluetooth permission request.")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), 99)
            }
        }

        /*Bluetooth Adapter and Manager declaration */
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        /*layout xml details*/
        onOffButton = findViewById(R.id.OnOffButton)
        status = findViewById(R.id.BluetoothStatus)
        onOffImage = findViewById(R.id.OnOff_Image)
        showDevicesNearby = findViewById(R.id.showDevicesNearby)
        listPairedDevices = findViewById(R.id.listPairedDevice)

        /* only shows Toast text once */
        if (bluetoothAdapter == null) {
            Toast.makeText(
                applicationContext,
                "Bluetooth is not available on this device",
                Toast.LENGTH_SHORT
            ).show()
            statusUnavailable()
        }

        /* Bluetooth check every 3 seconds
        * status check instead of re-creating the page over*/
        val bluetoothCheck = Handler()
        val bluetoothCheckRun = object : Runnable {
            override fun run() {
                /* check if BLUETOOTH availability*/
                when (bluetoothAdapter.isEnabled) {
                    true -> {
                        listPairedDevices = findViewById(R.id.listPairedDevice)
                        statusOn()
                        getBluetoothPairedDevices(mDeviceList,listPairedDevices)
                    }
                    false -> statusOff()
                    else -> statusUnavailable()
                }

                bluetoothCheck.postDelayed(this, 1000)
            }
        }
        bluetoothCheck.postDelayed(bluetoothCheckRun, 1000)

        onOffButton.setOnClickListener {
            when (bluetoothAdapter.isEnabled) {
                true -> {
                    bluetoothAdapter.disable()
                    listPairedDevices.adapter = null
                    Toast.makeText(applicationContext, "Turned off Bluetooth", Toast.LENGTH_SHORT).show()
                }
                false -> {
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
                    Toast.makeText(
                        applicationContext,
                        "Bluetooth is not available on this device",
                        Toast.LENGTH_SHORT
                    ).show()
                    statusUnavailable()
                }
            }
        }

        showDevicesNearby.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                val intentOpenBluetoothSettings = Intent()
                intentOpenBluetoothSettings.action = Settings.ACTION_BLUETOOTH_SETTINGS
                startActivity(intentOpenBluetoothSettings)
            } else if (!bluetoothAdapter.isEnabled) {
                Log.d("MyHeartBeat", "List Bluetooth devices failed : Bluetooth is disabled")
                Toast.makeText(
                    applicationContext,
                    "Please enable Bluetooth first",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.d("MyHeartBeat", "List Bluetooth devices failed : Bluetooth unavailable")
                Toast.makeText(
                    applicationContext,
                    "Bluetooth is unavailable on this device",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            99 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MyHeartBeat", "BLUETOOTH permission granted")
                } else {
                    Toast.makeText(
                        applicationContext,
                        "App requires Bluetooth permissions",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("MyHeartBeat", "BLUETOOTH permission not granted")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        when (requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (requestCode == Activity.RESULT_OK) {
                    Log.d("MyHeartBeat", "Bluetooth enable approved")
                } else {
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

    private fun statusOn() {
        Log.d("MyHeartBeat", "BLUETOOTH status on")
        status.setText(R.string.bluetooth_status_on)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_on)
        onOffButton.setText(R.string.turn_off)
    }

    private fun statusOff() {
        Log.d("MyHeartBeat", "BLUETOOTH status off")
        status.setText(R.string.bluetooth_status_off)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
        onOffButton.setText(R.string.turn_on)
    }

    private fun statusUnavailable() {
        Log.d("MyHeartBeat", "BLUETOOTH status unavailable")
        status.setText(R.string.bluetooth_not_supported)
        onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
        onOffButton.setText(R.string.turn_on)
    }

    @SuppressLint("MissingPermission")
    private fun getBluetoothPairedDevices(deviceList: ArrayList<String>, listView: ListView) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(
                applicationContext,
                "This device not support bluetooth",
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                val enableAdapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableAdapter, 0)
            }
            val all_devices = bluetoothAdapter.bondedDevices
            if(devices == all_devices.size){
                return
            } else if (all_devices.size > 0) {
                devices = 0
                deviceList.clear()
                for (currentDevice in all_devices) {
                    devices += 1
                    deviceList.add(
                        """
                        Device Name: ${currentDevice.name}
                        Device Address: ${currentDevice.address}
                        """.trimIndent()
                    )
                    listView.adapter = ArrayAdapter<Any?>(
                        application,
                        android.R.layout.simple_list_item_1, deviceList as List<Any?>
                    )
                }
            }
        }
    }
}
