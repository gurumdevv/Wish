package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.storage.StorageReference
import com.gurumlab.wish.data.auth.FirebaseAuthManager
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
    private val storageRef: StorageReference,
    private val authManager: FirebaseAuthManager
) {

    fun getCurrentUser() = currentUser

    fun getUid() = currentUser?.uid ?: ""

    fun logOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount(googleIdToken: String, idToken: String, uid: String): Flow<Boolean> = flow {
        val response = apiClient.getPostsByUid(
            orderBy = "\"${Constants.POSTER_ID}\"",
            equalTo = "\"${uid}\"",
            idToken = idToken
        )
        response.onSuccess {
            val failedWishes = mutableListOf<String>()

            it.keys.forEach { wishId ->
                try {
                    apiClient.deleteWish(wishId = wishId, idToken = idToken)
                    storageRef.child(Constants.IMAGES).child(wishId).delete()
                        .addOnFailureListener {
                            failedWishes.add(wishId)
                        }
                } catch (e: Exception) {
                    failedWishes.add(wishId) //실패하면 ID Token 유효성이 중간에 상실됐는지 확인하기
                    Log.d("SettingsRepository", "Fail to delete wish: ${e.message}")
                }
            }

            emit(
                if (failedWishes.isEmpty()) removeFirebaseAuthUser(
                    googleIdToken = googleIdToken,
                    user = currentUser!!
                ) else false
            )
        }.onError { _, _ ->
            emit(
                removeFirebaseAuthUser(
                    googleIdToken = googleIdToken,
                    user = currentUser!!
                )
            )
        }.onException {
            emit(false)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun removeFirebaseAuthUser(googleIdToken: String, user: FirebaseUser): Boolean {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
            user.reauthenticate(credential).await()
            user.delete().await()
            true
        } catch (e: Exception) {
            Log.d("SettingsRepository", "Fail to delete user: ${e.message}")
            false
        }
    }

    fun getWishes(
        idToken: String,
        orderBy: String,
        equalTo: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response =
            apiClient.getPostsByUid(
                orderBy = "\"${orderBy}\"",
                equalTo = "\"${equalTo}\"",
                idToken = idToken
            )
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

    suspend fun deleteWish(idToken: String, wishId: String) {
        try {
            apiClient.deleteWish(wishId = wishId, idToken = idToken)
        } catch (e: Exception) {
            Log.d("deleteWish", "Error delete Wish: ${e.message}")
        }
    }

    suspend fun getFirebaseIdToken(): String {
        return authManager.getFirebaseIdToken()
    }
}