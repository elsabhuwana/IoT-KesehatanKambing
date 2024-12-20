package com.example.firebase

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var suhuValue: TextView
    private lateinit var kelembabanValue: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Menambahkan Toolbar sebagai ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Monitoring Realtime"

        suhuValue = findViewById(R.id.tvSuhuValue)
        kelembabanValue = findViewById(R.id.tvKelembabanValue)
        database = FirebaseDatabase.getInstance().reference

        // Mengambil data dari Firebase
        database.child("DATA_SENSOR").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val suhu = snapshot.child("SUHU").getValue(Int::class.java)
                val kelembaban = snapshot.child("KELEMBABAN").getValue(Int::class.java)

                // Menampilkan data pada TextView
                suhuValue.text = "$suhu°C"
                kelembabanValue.text = "$kelembaban%"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
