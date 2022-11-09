package com.example.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class History_pg : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_pg)
        Log.d("MyHeartBeat", "History page create")

        val historyList = findViewById<ListView>(R.id.HistoryDisplay)
        historyList.setBackgroundColor(Color.TRANSPARENT)
        val listItems = resources.getStringArray(R.array.sampleHistory)
        val historyAdapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listItems)
        historyList.adapter = historyAdapter
    }
}