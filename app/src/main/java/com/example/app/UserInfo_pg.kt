package com.example.app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class UserInfo_pg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_pg)

        var chgButton = findViewById<Button>(R.id.changeName)
        var nameDisplay = findViewById<EditText>(R.id.UserNameDisplay)
        var underline = findViewById<View>(R.id.underlineName)
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