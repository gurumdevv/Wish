package com.gurumlab.wish.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val currentUser: FirebaseUser?,
    private val database: FirebaseDatabase,
    private val fireStore: FirebaseFirestore
) {

    fun getCurrentUser() = currentUser

    fun getDatabaseReference() = database.reference

    fun getFireStore() = fireStore
}