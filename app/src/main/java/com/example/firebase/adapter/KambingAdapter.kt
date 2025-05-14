package com.example.firebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.R
import com.example.firebase.model.KambingModel

class KambingAdapter(
    private val kambingList: List<KambingModel>,
    private val onItemClick: (KambingModel) -> Unit
) : RecyclerView.Adapter<KambingAdapter.KambingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KambingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kambing, parent, false)
        return KambingViewHolder(view)
    }

    override fun onBindViewHolder(holder: KambingViewHolder, position: Int) {
        val kambing = kambingList[position]
        holder.tvNamaKambing.text = kambing.namaKambing
        holder.tvSuhuTubuh.text = kambing.suhuTubuh
        holder.tvDetakJantung.text = kambing.detakJantung

        // Atur indikator baterai sesuai dengan nilai baterai
        val batteryLevel = kambing.baterai
        when {
            batteryLevel > 75 -> holder.imgBaterai.setImageResource(R.drawable.baseline_battery_5_bar_24)
            batteryLevel > 50 -> holder.imgBaterai.setImageResource(R.drawable.baseline_battery_4_bar_24)
            batteryLevel > 25 -> holder.imgBaterai.setImageResource(R.drawable.baseline_battery_3_bar_24)
            else -> holder.imgBaterai.setImageResource(R.drawable.baseline_battery_2_bar_24)
        }

        holder.itemView.setOnClickListener {
            onItemClick(kambing)
        }
    }

    override fun getItemCount(): Int = kambingList.size

    class KambingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaKambing: TextView = view.findViewById(R.id.tvNamaKambing)
        val tvSuhuTubuh: TextView = view.findViewById(R.id.tvSuhuTubuh)
        val tvDetakJantung: TextView = view.findViewById(R.id.tvDetakJantung)
        val imgBaterai: ImageView = view.findViewById(R.id.imgBaterai)
    }
}
