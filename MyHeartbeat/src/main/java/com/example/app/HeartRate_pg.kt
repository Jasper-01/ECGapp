@file:Suppress("DEPRECATION")

package com.example.app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

class HeartRate_pg : ThemeChange() {

    val floatSize = Float.SIZE_BYTES

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothThing : BluetoothDevice
    private var bluetoothSocket : BluetoothSocket? = null

    val mUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private lateinit var series: LineGraphSeries<DataPoint>
    private lateinit var graph: GraphView
    private lateinit var viewport: Viewport

    private lateinit var textClock : TextClock
    private lateinit var savebtn : Button
    private lateinit var bpm : TextView

    var x = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_heart_rate_pg)
        Log.d("MyHeartBeat", "HeartRate page created")

        val backBtn: View = findViewById<Button>(R.id.backbtn)
        backBtn.setOnClickListener {
            finish()
        }

        /* check bluetooth permissions*/
        if(!BluetoothObj.checkBTpermissions(this)) {
            Toast.makeText(applicationContext, "Please allow bluetooth permissions", Toast.LENGTH_SHORT).show()
            BluetoothObj.requestBTPermissions(this)
        }
        
        textClock = findViewById(R.id.Time)
        savebtn = findViewById(R.id.button)
        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        graph = findViewById<View>(R.id.heartRateGraph) as GraphView
        series = LineGraphSeries()
        viewport = graph.viewport

        graph = findViewById<View>(R.id.heartRateGraph) as GraphView
        series = LineGraphSeries()
        viewport = graph.viewport

        viewport.isYAxisBoundsManual = true
        viewport.isXAxisBoundsManual = true
        viewport.setMinX(0.0)
        viewport.setMaxX(3.0)
        viewport.setMinY(0.0)
//        viewport.setMaxY(1.0)
        viewport.setMaxY(600.0)

        viewport.isScrollable = true
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        graph.gridLabelRenderer.isVerticalLabelsVisible = false

        doBluetoothStuff(graph)

        savebtn.setOnClickListener{
            val bpm  = findViewById<TextView>(R.id.HeartRateStats).text.toString()
            val email = sharedPreferences.getString("email", "0").toString()
            val full_name = sharedPreferences.getString("full_name", "0").toString()
            val text_clc = textClock.text.toString()
            editor.putString("date_time", textClock.text.toString())
            editor.apply()

            saveFireStore(bpm, text_clc, email, full_name)
        }
    }

    override fun onDestroy() {
        try {
            bluetoothSocket?.close()
            Log.d("MyHeartBeat", "Bluetooth Socket close successful")
        } catch (e : IOException){
            Log.d("MyHeartBeat", "Bluetooth Socket close unsuccessful")
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun saveFireStore(bpm: String, text_clc: String, email:String, full_name : String){
        val db = Firebase.firestore
        val user = hashMapOf(
            "bpm" to bpm,
            "datetime" to text_clc,
            "name" to full_name
        )

        if(email == "0"){
            Toast.makeText(applicationContext, "Need G-MAIL SIGNUP to save", Toast.LENGTH_SHORT).show()
        }
        else{
            db.collection(email)
                .add(user)
                .addOnSuccessListener {
                    Log.d(TAG, "Document Added")
                    Toast.makeText(applicationContext, "Successfully saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error adding document")
                    Toast.makeText(applicationContext, "Error saving", Toast.LENGTH_SHORT).show()
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun doBluetoothStuff(graph : GraphView) {

        bpm = findViewById(R.id.HeartRateStats)

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothSocket = null

        var y : Double

        when(bluetoothAdapter.isEnabled){
            null ->{
                Toast.makeText(
                    applicationContext,
                    "This device not support bluetooth",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("MyHeartBeat", "Bluetooth unsupported on current device")
            }

            false -> {
                Toast.makeText(applicationContext, "Please enable bluetooth first.", Toast.LENGTH_SHORT).show()
                Log.d("MyHeartBeat", "Bluetooth disabled on current device")
            }

            true -> {
                bluetoothThing = bluetoothAdapter.getRemoteDevice("98:DA:60:02:B9:DC")
//                bluetoothThing = bluetoothAdapter.getRemoteDevice("98:DA:60:02:6C:CD")
                var counter = 0
                do{
                    try{
                        bluetoothSocket = bluetoothThing.createRfcommSocketToServiceRecord(mUUID)
                        bluetoothSocket?.connect()
                        Log.d("MyHeartBeat", "Connection success")
                    } catch (e : IOException){
                        e.printStackTrace()
                        Log.d("MyHeartBeat", "Connection failed")
                        Toast.makeText(applicationContext, "Please ensure the device is on and paired", Toast.LENGTH_SHORT).show()
                        return
                    }
                    counter++
                } while (!bluetoothSocket!!.isConnected && counter < 3)

                val handler = Handler()
                val runnable = object : Runnable {
                    var byteBuffer : ByteArray = ByteArray(floatSize)

                    override fun run() {
                        var inputStream: InputStream?

                        bpm.text = "--BPM"
                        try{
                            inputStream = bluetoothSocket!!.inputStream
                            inputStream.skip(inputStream.available().toLong())
                            x += 0.1
                            inputStream.read(byteBuffer)
                            y = String(byteBuffer, StandardCharsets.UTF_8).toDouble()
                            series.appendData(DataPoint(x, y), true, 500)
                            graph.addSeries(series)
                        } catch (e : IOException){
                            e.printStackTrace()
                            Log.d("MyHeartBeat", "No input data / Graph could not be plot")
                            return
                        }
                        handler.postDelayed(this, 100)
                    }
                }
                handler.postDelayed(runnable, 100)
            }
        }
    }
}
