package com.example.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.adapter.LogAdapter
import com.example.firebase.model.LogItem
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class LogFragment : Fragment() {

    private lateinit var spinnerKambing: Spinner
    private lateinit var recyclerViewLogs: RecyclerView
    private lateinit var database: FirebaseDatabase
    private lateinit var logAdapter: LogAdapter
    private val logs = mutableListOf<LogItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)

        spinnerKambing = view.findViewById(R.id.spinnerKambing)
        recyclerViewLogs = view.findViewById(R.id.recyclerViewLogs)

        database = FirebaseDatabase.getInstance()

        recyclerViewLogs.layoutManager = LinearLayoutManager(context)
        logAdapter = LogAdapter(logs)
        recyclerViewLogs.adapter = logAdapter

        setupSpinner()

        spinnerKambing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lastLogMessage = null
                clearLogs()
                getLogs()
                listenToSensorChanges()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Kandang", "Kambing ID_1", "Kambing ID_2")
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKambing.adapter = spinnerAdapter
    }

    private fun clearLogs() {
        logs.clear()  // Clear logs before adding new ones
        logAdapter.notifyDataSetChanged()  // Notify adapter about the change
    }

    private fun addLogToDatabase(sensorPath: String, message: String) {
        val logRef = when (sensorPath) {
            "Kandang" -> database.getReference("DATA_SENSOR/LOG/KANDANG")
            "Kambing ID_1" -> database.getReference("DATA_SENSOR/LOG/KAMBING/ID_1")
            "Kambing ID_2" -> database.getReference("DATA_SENSOR/LOG/KAMBING/ID_2")
            else -> null
        }

        logRef?.let {
            val formattedDate =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val newLogItem = LogItem(formattedDate, sensorPath, message)

            it.push().setValue(newLogItem)  // Add log without checking duplication
        }
    }

    private fun getLogs() {
        // Mendapatkan ID yang dipilih dari Spinner
        val selected = spinnerKambing.selectedItem.toString()

        val pathRef = when (selected) {
            "Kandang" -> database.getReference("DATA_SENSOR/LOG/KANDANG")
            "Kambing ID_1" -> database.getReference("DATA_SENSOR/LOG/KAMBING/ID_1")
            "Kambing ID_2" -> database.getReference("DATA_SENSOR/LOG/KAMBING/ID_2")
            else -> null
        }

        pathRef?.orderByChild("waktu")?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val waktu = snapshot.child("waktu").getValue(String::class.java)
                val tipe = snapshot.child("tipe").getValue(String::class.java)
                val pesan = snapshot.child("pesan").getValue(String::class.java)

                if (waktu != null && tipe != null && pesan != null) {
                    val newLog = LogItem(waktu, tipe, pesan)

                    // Cek apakah log sudah ada di dalam logs (untuk mencegah duplikasi di aplikasi)
                    if (!logs.contains(newLog)) {
                        logs.add(newLog)
                        logAdapter.notifyItemInserted(logs.size - 1)  // Update RecyclerView
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal mengambil data log: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private var lastLogMessage: String? = null
    private var sensorListener: ValueEventListener? = null
    private var currentSensorRef: DatabaseReference? = null


    private fun listenToSensorChanges() {
        val selected = spinnerKambing.selectedItem.toString()

        // Hapus listener sebelumnya
        currentSensorRef?.removeEventListener(sensorListener ?: return)

        val newRef = when (selected) {
            "Kandang" -> database.getReference("DATA_SENSOR/KANDANG")
            "Kambing ID_1" -> database.getReference("DATA_SENSOR/KAMBING/ID_1")
            "Kambing ID_2" -> database.getReference("DATA_SENSOR/KAMBING/ID_2")
            else -> null
        }

        if (newRef != null) {
            sensorListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var newMessage: String? = null

                    when (selected) {
                        "Kandang" -> {
                            val suhu = snapshot.child("SUHU").child("NILAI").getValue(Double::class.java)
                            val statusSuhu = snapshot.child("SUHU").child("STATUS").getValue(String::class.java)
                            val kelembaban = snapshot.child("KELEMBABAN").child("NILAI").getValue(Double::class.java)
                            val statusKelembaban = snapshot.child("KELEMBABAN").child("STATUS").getValue(String::class.java)
                            if (suhu != null && kelembaban != null) {
                                newMessage = "Suhu: $suhu ($statusSuhu), Kelembaban: $kelembaban ($statusKelembaban)"
                            }
                        }

                        "Kambing ID_1", "Kambing ID_2" -> {
                            val suhu = snapshot.child("SUHU_TUBUH").child("NILAI").getValue(Double::class.java)
                            val statusSuhu = snapshot.child("SUHU_TUBUH").child("STATUS").getValue(String::class.java)
                            val detak = snapshot.child("DETAK_JANTUNG").child("NILAI").getValue(Double::class.java)
                            val statusDetak = snapshot.child("DETAK_JANTUNG").child("STATUS").getValue(String::class.java)
                            if (suhu != null && detak != null) {
                                newMessage = "Suhu Tubuh: $suhu ($statusSuhu), Detak Jantung: $detak ($statusDetak)"
                            }
                        }
                    }

                    if (newMessage != null && newMessage != lastLogMessage) {
                        lastLogMessage = newMessage
                        addLogToDatabase(selected, newMessage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }

            // Simpan referensi listener dan database-nya
            currentSensorRef = newRef
            newRef.addValueEventListener(sensorListener as ValueEventListener)
        }
    }
}