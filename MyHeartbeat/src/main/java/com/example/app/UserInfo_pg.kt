package com.example.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class UserInfo_pg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_pg)
        Log.d("MyHeartBeat", "UserInfo page create")

        val chgButton = findViewById<Button>(R.id.changeName)
        val nameDisplay = findViewById<EditText>(R.id.UserNameDisplay)
        val underline = findViewById<View>(R.id.underlineName)
        nameDisplay.isEnabled = false

        chgButton.setOnClickListener {
            if(!nameDisplay.isEnabled){  // not enable = cannot edit
                nameDisplay.isEnabled = true  // can edit text
                chgButton.setText(R.string.save)
                underline.setBackgroundColor(Color.DKGRAY)
            }
            else{
                nameDisplay.isEnabled = false  // cannot edit text
                chgButton.setText(R.string.change)
                underline.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}