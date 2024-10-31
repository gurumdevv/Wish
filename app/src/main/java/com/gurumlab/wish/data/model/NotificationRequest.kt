package com.gurumlab.wish.data.model

data class NotificationRequest(
    val message: Message
)

data class Message(
    val token: String,
    val notification: Notification
)

data class Notification(
    val title: String,
    val body: String
)