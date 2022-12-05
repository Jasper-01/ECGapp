package com.example.app
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth

private lateinit var auth: FirebaseAuth

class HeartRate_pg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heart_rate_pg)
        Log.d("MyHeartBeat", "HeartRate page created")

        val backbtn = findViewById<Button>(R.id.backbtn)
        backbtn.setOnClickListener {
            finish()
        }

        val textClock = findViewById<TextClock>(R.id.Time)
        val savebtn = findViewById<Button>(R.id.button)
        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        textClock.format12Hour = null
        textClock.format24Hour = "yyyy - LL - dd (E) HH:mm:ss"

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
}