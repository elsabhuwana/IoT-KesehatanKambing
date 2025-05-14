package com.example.firebase.model

data class LogItem(
    val waktu: String,  // Timestamp
    val tipe: String,   // Tipe log (misalnya "Suhu", "Detak Jantung", dll)
    val pesan: String   // Pesan log (misalnya "Suhu tubuh kambing normal")
)

