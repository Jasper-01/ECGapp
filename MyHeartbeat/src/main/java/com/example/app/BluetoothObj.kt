package com.example.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object BluetoothObj {
    private val bluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH)
    } else {
        arrayOf(Manifest.permission.BLUETOOTH)
    }

    fun requestBTPermissions(activity: Activity){
        ActivityCompat.requestPermissions(activity, bluetoothPermissions, bluetoothPermissions.size)
    }

    fun checkBTpermissions(context : Context): Boolean {
        for(permissions in bluetoothPermissions){
            if(ContextCompat.checkSelfPermission(context, permissions) != PackageManager.PERMISSION_GRANTED){
                Log.d("MyHeartBeat", "Bluetooth permissions denied")
                return false
            }
        }
        return true
    }
}
