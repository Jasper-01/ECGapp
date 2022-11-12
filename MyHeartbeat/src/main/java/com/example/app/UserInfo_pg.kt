package com.example.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserInfo_pg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_pg)
        Log.d("MyHeartBeat", "UserInfo page create")

        var chgButton = findViewById<Button>(R.id.changeName)
        val nameDisplay = findViewById<EditText>(R.id.UserNameDisplay)
        val underline = findViewById<View>(R.id.underlineName)
        val ID = findViewById<TextView>(R.id.UserIDDisplay).text.toString()
        val name_text = nameDisplay.text.toString()
        val sharedPref = getSharedPreferences("myKey", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("value", ID)
        editor.putString("name", name_text)
        editor.apply()
        nameDisplay.setEnabled(false)

        chgButton.setOnClickListener {
            if(!nameDisplay.isEnabled){  // not enable = cannot edit
                nameDisplay.setEnabled(true)  // can edit text
                chgButton.setText(R.string.save)
                underline.setBackgroundColor(Color.DKGRAY)
            }
            else{
                nameDisplay.setEnabled(false)  // cannot edit text
                chgButton.setText(R.string.change)
                underline.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}