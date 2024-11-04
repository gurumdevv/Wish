package com.gurumlab.wish.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.gurumlab.wish.MainActivity
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.util.Constants

class WishFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        sendNotification(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendNotification(
        remoteMessage: RemoteMessage
    ) {
        val title = remoteMessage.data.getOrDefault(Constants.TITLE, "")
        val body = remoteMessage.data.getOrDefault(Constants.BODY, "")
        val chatRoomId = remoteMessage.data.getOrDefault(Constants.CHAT_ROOM_ID, "")

        val intent = Intent(this, MainActivity::class.java)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(Constants.CHAT_ROOM_ID, chatRoomId)
            }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_wishes)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (notificationManager.getNotificationChannel(channelId) == null) {
            createNotificationChannel(notificationManager)
        }

        notificationManager.notify(MESSAGE_NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        const val MESSAGE_NOTIFICATION_ID = 1001
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelId = getString(R.string.notification_channel_id)
        val channelName = getString(R.string.channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = getString(R.string.chat_notification_description)
        }

        notificationManager.createNotificationChannel(channel)
    }
}