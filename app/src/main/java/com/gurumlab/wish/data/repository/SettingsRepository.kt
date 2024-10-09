package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.source.remote.ApiClient
import com.gurumlab.wish.data.source.remote.onError
import com.gurumlab.wish.data.source.remote.onException
import com.gurumlab.wish.data.source.remote.onSuccess
import com.gurumlab.wish.ui.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val firebaseAuth: FirebaseAuth,
    private val currentUser: FirebaseUser?,
    private val storageRef: StorageReference
) {

    fun getUserInfo() = currentUser

    fun getUid() = currentUser?.uid ?: ""

    fun logOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount(uid: String): Flow<Boolean> = flow {
        val response = apiClient.getPostsByPosterId(
            orderBy = "\"${Constants.POSTER_ID}\"",
            equalTo = "\"${uid}\""
        )
        response.onSuccess {
            val failedWishes = mutableListOf<String>()

            it.keys.forEach { wishId ->
                try {
                    apiClient.deleteWish(wishId)
                    storageRef.child("images").child(wishId).delete()
                        .addOnFailureListener {
                            failedWishes.add(wishId)
                        }
                } catch (e: Exception) {
                    failedWishes.add(wishId)
                }
            }

            emit(if (failedWishes.isEmpty()) removeFirebaseAuthUser(currentUser!!) else false)
        }.onError { _, _ ->
            emit(removeFirebaseAuthUser(currentUser!!))
        }.onException {
            emit(false)
        }
    }

    private suspend fun removeFirebaseAuthUser(user: FirebaseUser): Boolean {
        return try {
            user.delete().await()
            true
        } catch (e: Exception) {
            Log.d("SettingsRepository", "Fail to delete user: ${e.message}")
            false
        }
    }

    fun getWishes(
        orderBy: String,
        equalTo: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response =
            apiClient.getPostsByUserId("\"${orderBy}\"", "\"${equalTo}\"")
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            emit(emptyMap())
            onError("code: $code, message: $message")
        }.onException {
            emit(emptyMap())
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteWish(wishId: String) {
        try {
            apiClient.deleteWish(wishId)
        } catch (e: Exception) {
            Log.d("deleteWish", "Error delete Wish: ${e.message}")
        }
    }
}