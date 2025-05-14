package com.example.firebase.model

// Menghapus kelas KambingModel yang tidak perlu
data class KambingModel(
    val id: String,
    val namaKambing: String,
    val suhuTubuh: String,
    val detakJantung: String,
    val baterai: Int
)
