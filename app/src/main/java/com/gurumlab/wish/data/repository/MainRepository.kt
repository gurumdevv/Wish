package com.gurumlab.wish.data.repository

import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val currentUser: FirebaseUser?,
) {

    fun getCurrentUser() = currentUser
}