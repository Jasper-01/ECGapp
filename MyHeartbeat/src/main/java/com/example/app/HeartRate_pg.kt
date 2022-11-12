package com.example.app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HeartRate_pg : AppCompatActivity() {
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


        savebtn.setOnClickListener{
            val bpm  = findViewById<TextView>(R.id.HeartRateStats).text.toString()
            val ID_value = sharedPreferences.getString("value", "0").toString()
            val name = sharedPreferences.getString("name", "0").toString()
            val text_clc = textClock.text.toString()
            editor.putString("date_time", textClock.text.toString())
            editor.apply()

            saveFireStore(bpm, text_clc, ID_value, name)
        }
    }

    private fun saveFireStore(bpm: String, text_clc: String, ID_value: String, name: String){
        val db = Firebase.firestore
        val user = hashMapOf(
            "bpm" to bpm,
            "datetime" to text_clc,
            "name" to name
        )

        db.collection(name +"_"+ ID_value)
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