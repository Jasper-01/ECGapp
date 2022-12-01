package com.example.app

import android.R.attr.data
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

class History_pg : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<user>
    private lateinit var adapter: adapter
    private lateinit var db: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_pg)
        Log.d("MyHeartBeat", "History page created")
//        val historyList = findViewById<ListView>(R.id.HistoryDisplay)
//        historyList.setBackgroundColor(Color.TRANSPARENT)
        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val ID_value = sharedPreferences.getString("value", "0").toString()
        val name = sharedPreferences.getString("name", "0").toString()
        val date = sharedPreferences.getString("date_time", "0").toString()
        val email = sharedPreferences.getString("email", "0").toString()
/*        val listItems = resources.getStringArray(R.array.sampleHistory)
        val historyAdapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, listItems)
        historyList.adapter = historyAdapter*/

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()
        adapter = adapter(userArrayList)
        recyclerView.adapter = adapter
        EventChangeListener(name, ID_value, date, email)

    }

    private fun EventChangeListener(name: String, ID_value: String, date: String, email :String){

        db = FirebaseFirestore.getInstance()
        db.collection(email).
        addSnapshotListener(object: EventListener<QuerySnapshot>{
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error != null) {
                    Log.e("Firestore Error", error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        userArrayList.add(dc.document.toObject(user::class.java))
                    }
                }
                userArrayList.sortByDescending {
                    it.datetime
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}