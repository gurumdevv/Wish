package com.gurumlab.wish.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val currentUser: FirebaseUser?,
    private val fireStore: FirebaseFirestore,
    private val firebaseDatabaseRef: DatabaseReference
) {

    fun getCurrentUser() = currentUser

    fun getFireStore() = fireStore

    fun getFirebaseDatabaseRef() = firebaseDatabaseRef
}