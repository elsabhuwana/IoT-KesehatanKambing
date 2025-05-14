package com.example.firebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.R
import com.example.firebase.model.LogItem

class LogAdapter(private val logs: List<LogItem>) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val logItem = logs[position]
        holder.waktuTextView.text = logItem.waktu
        holder.tipeTextView.text = logItem.tipe
        holder.pesanTextView.text = logItem.pesan
    }

    override fun getItemCount(): Int = logs.size

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val waktuTextView: TextView = view.findViewById(R.id.textViewWaktu)
        val tipeTextView: TextView = view.findViewById(R.id.textViewTipe)
        val pesanTextView: TextView = view.findViewById(R.id.textViewPesan)
    }
}
