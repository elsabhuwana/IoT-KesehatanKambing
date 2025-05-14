package com.example.firebase

import android.Manifest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            // Tangani data pesan di sini
        }

        if (remoteMessage.notification != null) {
            val notification = remoteMessage.notification
            val title = notification?.title
            val body = notification?.body

            // Buat notifikasi menggunakan NotificationManager
            val builder = NotificationCompat.Builder(this, "alert_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, builder.build())
        }
    }
}
