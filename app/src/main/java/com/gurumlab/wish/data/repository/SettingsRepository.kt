package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
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
    private val databaseRef: DatabaseReference,
    private val authManager: FirebaseAuthManager
) {

    fun getCurrentUser() = currentUser

    fun getUid() = currentUser?.uid ?: ""

    fun logOut() {
        firebaseAuth.signOut()
    }

    fun getWishesByUid(
        idToken: String,
        uid: String,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>?> = flow {
        val response =
            apiClient.getPostsByUid(
                orderBy = "\"${Constants.POSTER_ID}\"",
                equalTo = "\"${uid}\"",
                idToken = idToken
            )

        response.onSuccess {
            emit(it)
        }.onError { _, _ ->
            emit(emptyMap<String, Wish>())
        }.onException {
            emit(null)
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun submitDeactivatedUserPostIds(uid: String, postIds: List<String>): Boolean {
        return try {
            databaseRef.child(Constants.DELETED_POSTS).child(uid).setValue(postIds).await()
            true
        } catch (e: Exception) {
            Log.d("settingsRepository", "Fail to upload postIds: ${e.message}")
            false
        }
    }

    suspend fun removeFirebaseAuthUser(googleIdToken: String, user: FirebaseUser): Boolean {
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

    suspend fun deleteWish(idToken: String, wishId: String): Boolean {
        try {
            apiClient.deleteWish(wishId = wishId, idToken = idToken)
            return true
        } catch (e: Exception) {
            Log.d("deleteWish", "Error delete Wish: ${e.message}")
            return false
        }
    }

    suspend fun getFirebaseIdToken(): String {
        return authManager.getFirebaseIdToken()
    }
}