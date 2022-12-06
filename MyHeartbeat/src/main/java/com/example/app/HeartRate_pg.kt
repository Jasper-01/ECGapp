package com.example.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.io.IOException
import java.io.InputStream
import java.util.*

private lateinit var auth: FirebaseAuth

class HeartRate_pg : AppCompatActivity() {

    val doubleSize = Double.SIZE_BYTES

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
        setContentView(R.layout.activity_heart_rate_pg)
        Log.d("MyHeartBeat", "HeartRate page created")

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
                Log.d("MyHeartBeat", "BLUETOOTH permission request.")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH), 99)
            }
        }

        textClock = findViewById(R.id.Time)
        savebtn = findViewById(R.id.button)
        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        textClock.format12Hour = null
        textClock.format24Hour = "yyyy - LL - dd (E) HH:mm:ss"

        graph = findViewById<View>(R.id.heartRateGraph) as GraphView
        series = LineGraphSeries()
        viewport = graph.viewport

        viewport.isYAxisBoundsManual = true
        viewport.isXAxisBoundsManual = true
        viewport.setMinX(0.0)
        viewport.setMaxX(3.0)
        viewport.setMinY(0.0)
        viewport.setMaxY(1.0)
//        viewport.setMaxY(10.0)
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

    private fun saveFireStore(bpm: String, text_clc: String, email:String, full_name : String) {

        auth = FirebaseAuth.getInstance()
        auth.fetchSignInMethodsForEmail(email)

        if(auth.getCurrentUser() != null){

            val db = Firebase.firestore
            val user = hashMapOf(
                "bpm" to bpm,
                "datetime" to text_clc,
                "name" to full_name
            )

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
        else{
            Toast.makeText(applicationContext, "Please Sign Up for Saving", Toast.LENGTH_SHORT).show()
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
                var counter = 0
                do{
                    try{
                        bluetoothSocket = bluetoothThing.createRfcommSocketToServiceRecord(mUUID)
                        println(bluetoothSocket)
                        bluetoothSocket!!.connect()
                        println(bluetoothSocket!!.isConnected)
                    } catch (e : IOException){
                        e.printStackTrace()
                    }
                    counter++
                } while (!bluetoothSocket!!.isConnected && counter < 3)

                val handler = Handler()
                val runnable = object : Runnable {
                    override fun run() {
                        var inputStream: InputStream?
                        var byteBuffer : ByteArray = byteArrayOf()

                        bpm.text = "--BPM"
                        try{
                            inputStream = bluetoothSocket!!.inputStream
                            inputStream.skip(inputStream.available().toLong())

                            for (i in 0..100) {
                                this@HeartRate_pg.runOnUiThread {
//                                    for(j in 0..doubleSize) {
//                                        byteBuffer[j] = inputStream.read().toByte()
//                                    }
//                                    y = byteBuffer.toString().toDouble()
                                    y = 0.1
                                    x += 0.01
                                    series.appendData(DataPoint(x, y), true, 300)
                                }
                            }
                        } catch (e : IOException){
                            e.printStackTrace()
                            Log.d("MyHeartBeat", "No input data / Graph could not be plot")
                        }
                        graph.addSeries(series)
                        handler.postDelayed(this, 1000)
                    }
                }
                handler.postDelayed(runnable, 1000)
            }
        }
    }
}