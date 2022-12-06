package com.example.app
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
        val sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "0").toString()

        val backBtn: View = findViewById<Button>(R.id.backbtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, Main_pg::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()
        adapter = adapter(userArrayList)
        recyclerView.adapter = adapter
        EventChangeListener(email)
    }

    private fun EventChangeListener(email :String){

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