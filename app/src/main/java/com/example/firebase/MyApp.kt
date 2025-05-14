package com.example.firebase

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Subscribe ke topik hanya sekali saat aplikasi dijalankan
        FirebaseMessaging.getInstance().subscribeToTopic("kandang")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Berhasil subscribe ke topik kandang")
                } else {
                    Log.e("FCM", "Gagal subscribe ke topik kandang", task.exception)
                }
            }

        FirebaseMessaging.getInstance().subscribeToTopic("kambing")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Berhasil subscribe ke topik kambing")
                } else {
                    Log.e("FCM", "Gagal subscribe ke topik kambing", task.exception)
                }
            }
    }
}
