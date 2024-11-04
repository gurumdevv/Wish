package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.gurumlab.wish.util.MessagingUtil
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val currentUser: FirebaseUser?,
    private val fireStore: FirebaseFirestore,
    private val firebaseDatabaseRef: DatabaseReference,
    private val messagingUtil: MessagingUtil
) {

    fun getCurrentUser() = currentUser

    fun getFireStore() = fireStore

    fun getFirebaseDatabaseRef() = firebaseDatabaseRef

    suspend fun sendPushMessage(
        chatRoomId: String,
        token: String,
        title: String,
        body: String
    ): Boolean {
        return try {
            messagingUtil.sendCommonMessage(
                chatRoomId = chatRoomId,
                othersFcmToken = token,
                title = title,
                body = body
            )
            true
        } catch (e: Exception) {
            Log.d("Messaging Service", "Failed to send push message: ${e.message}")
            false
        }
    }
}