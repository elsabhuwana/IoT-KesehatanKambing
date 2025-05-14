package com.example.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var txtBateraiKandang: TextView
    private lateinit var txtBateraiKambing: TextView

    private lateinit var txtSuhuKandang: TextView
    private lateinit var txtKelembaban: TextView
    private lateinit var txtSuhuTubuh: TextView
    private lateinit var txtDetakJantung: TextView

    private lateinit var statusSuhuKandang: TextView
    private lateinit var statusKelembaban: TextView
    private lateinit var statusSuhuTubuh: TextView
    private lateinit var statusDetakJantung: TextView

    private lateinit var cardSuhuKandang: LinearLayout
    private lateinit var cardKelembaban: LinearLayout
    private lateinit var cardSuhuTubuh: LinearLayout
    private lateinit var cardDetakJantung: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val idKambing = intent.getStringExtra("KAMBING_ID") ?: "ID_1"

        database = FirebaseDatabase.getInstance().reference

        txtBateraiKandang = findViewById(R.id.txtBateraiKandang)
        txtBateraiKambing = findViewById(R.id.txtBateraiKambing)

        txtSuhuKandang = findViewById(R.id.txtNilaiSuhuKandang)
        txtKelembaban = findViewById(R.id.txtNilaiKelembaban)
        txtSuhuTubuh = findViewById(R.id.txtNilaiSuhuTubuh)
        txtDetakJantung = findViewById(R.id.txtNilaiDetakJantung)

        statusSuhuKandang = findViewById(R.id.statusSuhuKandang)
        statusKelembaban = findViewById(R.id.statusKelembaban)
        statusSuhuTubuh = findViewById(R.id.statusSuhuTubuh)
        statusDetakJantung = findViewById(R.id.statusDetakJantung)

        cardSuhuKandang = findViewById(R.id.cardSuhuKandang)
        cardKelembaban = findViewById(R.id.cardKelembaban)
        cardSuhuTubuh = findViewById(R.id.cardSuhuTubuh)
        cardDetakJantung = findViewById(R.id.cardDetakJantung)

        ambilData(idKambing)

        cardSuhuKandang.setOnClickListener {
            showInfoPopup("Suhu Kandang", txtSuhuKandang.text.toString(), statusSuhuKandang.text.toString(), "Normal: 28°C - 33°C")
        }

        cardKelembaban.setOnClickListener {
            showInfoPopup("Kelembaban", txtKelembaban.text.toString(), statusKelembaban.text.toString(), "Normal: 60% - 80%")
        }

        cardSuhuTubuh.setOnClickListener {
            showInfoPopup("Suhu Tubuh", txtSuhuTubuh.text.toString(), statusSuhuTubuh.text.toString(), "Normal: 38°C - 39°C")
        }

        cardDetakJantung.setOnClickListener {
            showInfoPopup("Detak Jantung", txtDetakJantung.text.toString(), statusDetakJantung.text.toString(), "Normal: 70 - 80 BPM")
        }
    }

    private fun ambilData(idKambing: String) {
        val kambingRef = database.child("DATA_SENSOR").child("KAMBING").child(idKambing)
        val kandangRef = database.child("DATA_SENSOR").child("KANDANG")

        kambingRef.child("SUHU_TUBUH").child("NILAI").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val suhu = snapshot.getValue(Double::class.java) ?: return
                txtSuhuTubuh.text = "$suhu°C"
                when {
                    suhu < 38 -> {
                        statusSuhuTubuh.text = "😟 Waspada"
                        cardSuhuTubuh.setBackgroundColor(Color.parseColor("#FFF59D"))
                    }
                    suhu in 38.0..39.0 -> {
                        statusSuhuTubuh.text = "😊 Aman"
                        cardSuhuTubuh.setBackgroundColor(Color.parseColor("#A5D6A7"))
                    }
                    else -> {
                        statusSuhuTubuh.text = "😱 Bahaya"
                        cardSuhuTubuh.setBackgroundColor(Color.parseColor("#EF9A9A"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        kambingRef.child("DETAK_JANTUNG").child("NILAI").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val detak = snapshot.getValue(Int::class.java) ?: return
                txtDetakJantung.text = "$detak BPM"
                when {
                    detak < 70 -> {
                        statusDetakJantung.text = "😟 Waspada"
                        cardDetakJantung.setBackgroundColor(Color.parseColor("#FFF59D"))
                    }
                    detak in 70..80 -> {
                        statusDetakJantung.text = "😊 Aman"
                        cardDetakJantung.setBackgroundColor(Color.parseColor("#A5D6A7"))
                    }
                    else -> {
                        statusDetakJantung.text = "😱 Bahaya"
                        cardDetakJantung.setBackgroundColor(Color.parseColor("#EF9A9A"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        kambingRef.child("BATERAI_KAMBING").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val baterai = snapshot.getValue(Int::class.java)
                baterai?.let {
                    txtBateraiKambing.text = "🔋 $it%"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        kandangRef.child("SUHU").child("NILAI").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val suhu = snapshot.getValue(Double::class.java) ?: return
                txtSuhuKandang.text = "${suhu.toInt()}°C"
                when {
                    suhu < 28 -> {
                        statusSuhuKandang.text = "😟 Waspada"
                        cardSuhuKandang.setBackgroundColor(Color.parseColor("#FFF59D"))
                    }
                    suhu in 28.0..33.0 -> {
                        statusSuhuKandang.text = "😊 Aman"
                        cardSuhuKandang.setBackgroundColor(Color.parseColor("#A5D6A7"))
                    }
                    else -> {
                        statusSuhuKandang.text = "😱 Bahaya"
                        cardSuhuKandang.setBackgroundColor(Color.parseColor("#EF9A9A"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        kandangRef.child("KELEMBABAN").child("NILAI").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kelembaban = snapshot.getValue(Int::class.java) ?: return
                txtKelembaban.text = "$kelembaban%"
                when {
                    kelembaban < 60 -> {
                        statusKelembaban.text = "😟 Waspada"
                        cardKelembaban.setBackgroundColor(Color.parseColor("#FFF59D"))
                    }
                    kelembaban in 60..80 -> {
                        statusKelembaban.text = "😊 Aman"
                        cardKelembaban.setBackgroundColor(Color.parseColor("#A5D6A7"))
                    }
                    else -> {
                        statusKelembaban.text = "😱 Bahaya"
                        cardKelembaban.setBackgroundColor(Color.parseColor("#EF9A9A"))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        kandangRef.child("BATERAI_KANDANG").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val baterai = snapshot.getValue(Int::class.java)
                baterai?.let {
                    txtBateraiKandang.text = "🔋 $it%"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    @SuppressLint("MissingInflatedId")
    private fun showInfoPopup(title: String, value: String, status: String, normalRange: String) {
        val view = layoutInflater.inflate(R.layout.popup_info, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        view.findViewById<TextView>(R.id.txtTitle).text = title
        view.findViewById<TextView>(R.id.txtValue).text = value
        view.findViewById<TextView>(R.id.txtStatus).text = status
        view.findViewById<TextView>(R.id.txtNormalRange).text = normalRange

        dialog.show()
    }
}
