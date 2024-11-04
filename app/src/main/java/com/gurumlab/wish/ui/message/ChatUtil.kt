package com.gurumlab.wish.ui.message

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.ui.util.Constants
import kotlinx.coroutines.tasks.await

fun moveToChatRoom(othersUid: String, callback: (ChatRoom, String, String, String) -> Unit) {
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
            callback(
                chatRoom,
                userinfo?.name ?: "",
                userinfo?.profileImageUrl ?: "",
                userinfo?.fcmToken ?: ""
            )
        }.addOnFailureListener {
            Log.e("MoveToChatRoom", "Error getting user info", it)
            callback(chatRoom, "", "", "")
        }
    }.addOnFailureListener {
        Log.e("MoveToChatRoom", "Error getting chat room", it)
    }
}

suspend fun getChatRoom(fireStoreReference: DocumentReference): ChatRoom? {
    return try {
        val chatRoomSnapshot = fireStoreReference.get().await()
        if (chatRoomSnapshot.exists()) {
            chatRoomSnapshot.toObject(ChatRoom::class.java) ?: ChatRoom()
        } else {
            ChatRoom()
        }
    } catch (e: Exception) {
        Log.d("getChatRoom", "Error getting chat room: ${e.message}")
        null
    }
}

suspend fun addMessage(
    uid: String,
    message: String,
    isSubmission: Boolean,
    messagesRef: DatabaseReference
): Boolean {
    val newChat = Chat(
        uid = uid,
        message = message,
        sentAt = System.currentTimeMillis(),
        submission = isSubmission
    )

    return try {
        messagesRef.push().setValue(newChat).await()
        true
    } catch (e: Exception) {
        Log.d("ChatViewModel", "Failed to add message: ${e.message}")
        false
    }
}

suspend fun getNotReadMessageCount(
    othersFireStoreRef: DocumentReference
): Int {
    try {
        val chatRoomSnapshot = othersFireStoreRef.get().await()
        val result = if (chatRoomSnapshot.exists()) {
            chatRoomSnapshot.toObject(ChatRoom::class.java) ?: ChatRoom()
        } else {
            ChatRoom()
        }
        return result.notReadMessageCount
    } catch (e: Exception) {
        Log.d("getNotReadMessageCount", "Error getting chat room: ${e.message}")
        return 0
    }
}

suspend fun updateChatRoom(
    message: String,
    chatRoom: ChatRoom,
    roomId: String,
    myUid: String,
    othersUid: String,
    myFireStoreRef: DocumentReference,
    othersFireStoreRef: DocumentReference
): Boolean {
    val notReadMessageCount = getNotReadMessageCount(othersFireStoreRef) + 1
    val currentTimeStamp = FieldValue.serverTimestamp()
    val batch = Firebase.firestore.batch()

    val myChatRoomData = mutableMapOf(
        Constants.LAST_MESSAGE to message,
        Constants.LAST_SENT_AT to currentTimeStamp
    ).apply {
        if (chatRoom.lastMessageSentAt == null) {
            put(Constants.ID, roomId)
            put(Constants.OTHERS_UID, othersUid)
            put(Constants.NOT_READ_MESSAGE_COUNT, 0)
        }
    }

    val othersChatRoomData = mutableMapOf(
        Constants.LAST_MESSAGE to message,
        Constants.LAST_SENT_AT to currentTimeStamp,
        Constants.NOT_READ_MESSAGE_COUNT to notReadMessageCount
    ).apply {
        if (chatRoom.lastMessageSentAt == null) {
            put(Constants.ID, roomId)
            put(Constants.OTHERS_UID, myUid)
            put(Constants.NOT_READ_MESSAGE_COUNT, 1)
        }
    }

    if (chatRoom.lastMessageSentAt == null) {
        batch.set(myFireStoreRef, myChatRoomData)
        batch.set(othersFireStoreRef, othersChatRoomData)
    } else {
        batch.update(myFireStoreRef, myChatRoomData)
        batch.update(othersFireStoreRef, othersChatRoomData)
    }

    return try {
        batch.commit().await()
        true
    } catch (e: Exception) {
        Log.d("updateFireStore", "Error updating chat room: ${e.message}")
        false
    }
}