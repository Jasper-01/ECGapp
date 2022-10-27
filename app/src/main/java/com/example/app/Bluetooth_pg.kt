package com.example.app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Bluetooth_pg : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 1

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_pg)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        val onOffButton = findViewById<Button>(R.id.OnOffButton)
        var status = findViewById<TextView>(R.id.BluetoothStatus)
        var onOffImage = findViewById<ImageView>(R.id.OnOff_Image)
        var deviceName = findViewById<TextView>(R.id.DeviceName)
        var deviceAddress = findViewById<TextView>(R.id.deviceAddress)
        val refreshButton = findViewById<ImageButton>(R.id.refreshButton)

        when (bluetoothAdapter?.isEnabled) {
            true -> {
                if (pairedDevices != null) {
                    status.setText(R.string.bluetooth_status_connected)
                    onOffImage.setImageResource(R.drawable.ic_bluetooth_connected)
                    pairedDevices?.forEach { device ->
                        deviceName.setText(device.name)
                        deviceAddress.setText(device.address) // MAC address
                    }
                } else {
                    status.setText(R.string.bluetooth_status_on)
                    onOffImage.setImageResource(R.drawable.ic_bluetooth_on)
                }
            }
            false -> {
                status.setText(R.string.bluetooth_status_off)
                onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
            }
            else -> {
                status.setText(R.string.bluetooth_not_supported)
                onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
            }
        }

        onOffButton.setOnClickListener {
            when (bluetoothAdapter?.isEnabled) {
                true -> {
                    bluetoothAdapter.disable()
                    onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
                    status.setText(R.string.bluetooth_status_off)
                    deviceName.setText(R.string.device_unavailable)
                    deviceAddress.setText(R.string.empty)
                }
                false -> {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                    if(callingActivity != null){
                        status.setText(R.string.bluetooth_status_on)
                        onOffImage.setImageResource(R.drawable.ic_bluetooth_on)
                    }
                    Handler().postDelayed({
                        this.recreate()
                    }, 3000)
                }
                else -> {
                    status.setText(R.string.bluetooth_not_supported)
                    onOffImage.setImageResource(R.drawable.ic_bluetooth_disable)
                }
            }
        }

        refreshButton.setOnClickListener {
            this.recreate()
        }
    }
}