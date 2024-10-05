package com.gurumlab.wish.ui.message

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.ui.util.Constants

fun moveToChatRoom(othersUid: String, callback: (ChatRoom, String, String) -> Unit) {
    val uid = Firebase.auth.currentUser?.uid ?: ""
    val roomId = "${uid}+${othersUid}"
    val firestore = Firebase.firestore
    val roomRef = firestore.collection(uid).document(roomId)

    val database = Firebase.database
    val otherUserInfoRef = database.getReference().child(Constants.AUTH).child(othersUid)

    roomRef.get().addOnSuccessListener { snapshot ->
        val chatRoom = if (snapshot.exists()) {
            snapshot.toObject(ChatRoom::class.java) ?: ChatRoom()
        } else {
            ChatRoom(
                id = roomId,
                othersUid = othersUid,
                lastMessage = "",
                notReadMessageCount = 0,
                lastMessageSentAt = null
            )
        }

        otherUserInfoRef.get().addOnSuccessListener {
            val userinfo = it.getValue(UserInfo::class.java)
            callback(chatRoom, userinfo?.name ?: "", userinfo?.profileImageUrl ?: "")
        }.addOnFailureListener {
            Log.e("MoveToChatRoom", "Error getting user info", it)
            callback(chatRoom, "", "")
        }
    }.addOnFailureListener {
        Log.e("MoveToChatRoom", "Error getting chat room", it)
    }
}