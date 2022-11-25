@file:Suppress("DEPRECATION")

package com.example.app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.Viewport
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries


class HeartRate_pg : AppCompatActivity(){
    private lateinit var series: LineGraphSeries<DataPoint>
    private lateinit var graph: GraphView
    private lateinit var viewport: Viewport

    var r = java.util.Random()

    var x = 0.0
    var y = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate_pg)
        Log.d("MyHeartBeat", "HeartRate page created")

        val textClock = findViewById<TextClock>(R.id.Time)
        val savebtn = findViewById<Button>(R.id.button)

        textClock.format12Hour = null
        textClock.format24Hour = "yyyy, LLLL dd (E) HH:mm:ss"

        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        graph = findViewById<View>(R.id.heartRateGraph) as GraphView
        series = LineGraphSeries()
        viewport = graph.viewport

        viewport.isYAxisBoundsManual = true
        viewport.isXAxisBoundsManual = true
        viewport.setMinX(0.0)
        viewport.setMaxX(3.0)
        viewport.setMinY(0.0)
        viewport.setMaxY(1.0)
        viewport.isScrollable = true
        graph.gridLabelRenderer.isHorizontalLabelsVisible = false
        graph.gridLabelRenderer.isVerticalLabelsVisible = false

        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                for (i in 0..100){
                    this@HeartRate_pg.runOnUiThread {
                        x+=0.01
                        y+=0.005
                        series.appendData(DataPoint(x, r.nextDouble()), true, 300)
//                        series.appendData(DataPoint(x, y), true, 150)
                    }
                }
                graph.addSeries(series)
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)

        savebtn.setOnClickListener{
            val bpm  = findViewById<TextView>(R.id.HeartRateStats).text.toString()
            val ID_value = sharedPreferences.getString("value", "0").toString()
            val name = sharedPreferences.getString("name", "0").toString()
            val email = sharedPreferences.getString("email", "0").toString()
            val full_name = sharedPreferences.getString("full_name", "0").toString()
            val text_clc = textClock.text.toString()
            editor.putString("date_time", textClock.text.toString())
            editor.apply()

            saveFireStore(bpm, text_clc, ID_value, name, email, full_name)
        }
    }

    private fun saveFireStore(bpm: String, text_clc: String, ID_value: String, name: String, email:String, full_name : String){
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
}