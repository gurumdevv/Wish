package com.gurumlab.wish.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ChatsRepository @Inject constructor(
    private val currentUser: FirebaseUser?,
    private val fireStore: FirebaseFirestore,
    private val firebaseDatabase: FirebaseDatabase
) {

    fun getCurrentUser() = currentUser

    fun getFireStore() = fireStore

    fun getFirebaseDatabase() = firebaseDatabase
}