package com.gurumlab.wish.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseAuthManager @Inject constructor(
    private val currentUser: FirebaseUser?,
) {

    suspend fun getFirebaseIdToken(): String {
        return if (currentUser == null) {
            ""
        } else {
            try {
                val result = currentUser.getIdToken(true).await()
                result.token ?: ""
            } catch (e: Exception) {
                Log.e("AuthManager", "Error getting Firebase ID token: ${e.message}")
                ""
            }
        }
    }
}