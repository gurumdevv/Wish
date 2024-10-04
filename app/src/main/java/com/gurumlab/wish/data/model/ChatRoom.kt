package com.gurumlab.wish.data.model

import com.google.firebase.Timestamp

data class ChatRoom(
    val id: String = "",
    val othersUid: String = "",
    val lastMessage: String = "",
    val notReadMessageCount: Int = 0,
    val lastMessageSentAt: Timestamp? = null
)