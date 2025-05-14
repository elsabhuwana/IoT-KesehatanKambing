package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.adapter.KambingAdapter
import com.example.firebase.model.KambingModel
import com.google.firebase.database.*
import com.example.firebase.MainActivity
import com.example.firebase.R

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewKambing)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        database = FirebaseDatabase.getInstance().getReference("DATA_SENSOR/KAMBING")

        fetchDataFromFirebase()

        return view
    }

    private fun fetchDataFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listKambing = mutableListOf<KambingModel>()
                for (data in snapshot.children) {
                    val id = data.key ?: ""
                    val suhuTubuh = data.child("SUHU_TUBUH/NILAI").value?.toString() ?: "-"
                    val detakJantung = data.child("DETAK_JANTUNG/NILAI").value?.toString() ?: "-"
                    val baterai = data.child("BATERAI").value?.toString()?.toIntOrNull() ?: 100

                    val kambing = KambingModel(id, "Kambing $id", suhuTubuh, detakJantung, baterai)
                    listKambing.add(kambing)
                }

                recyclerView.adapter = KambingAdapter(listKambing) { kambing ->
                    navigateToMainActivity(kambing.id)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToMainActivity(kambingId: String) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("KAMBING_ID", kambingId)
        startActivity(intent)
    }
}
