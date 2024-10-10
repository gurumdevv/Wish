package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.gurumlab.wish.data.auth.FirebaseAuthManager
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.source.remote.ApiClient
import com.gurumlab.wish.data.source.remote.onError
import com.gurumlab.wish.data.source.remote.onException
import com.gurumlab.wish.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DetailRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val currentUser: FirebaseUser?,
    private val authManager: FirebaseAuthManager
) {

    fun getPost(
        idToken: String,
        postId: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Wish?> = flow {
        val response = apiClient.getPostByPostId(postId = postId, idToken = idToken)
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            emit(null)
            onError("code: $code, message: $message")
        }.onException {
            emit(null)
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateStatus(idToken: String, postId: String, status: Int): Boolean {
        try {
            apiClient.updateStatus(postId = postId, status = status, idToken = idToken)
            return true
        } catch (e: Exception) {
            Log.d("updateWishStatus", "Error updating status: ${e.message}")
            return false
        }
    }

    suspend fun updateStartedDate(idToken: String, postId: String, startedDate: Int): Boolean {
        try {
            apiClient.updateStartedDate(
                postId = postId,
                startedDate = startedDate,
                idToken = idToken
            )
            return true
        } catch (e: Exception) {
            Log.d("updateStartedDate", "Error updating started date: ${e.message}")
            return false
        }
    }

    suspend fun updateDeveloperName(
        idToken: String,
        postId: String,
        developerName: String
    ): Boolean {
        try {
            apiClient.updateDeveloperName(
                postId = postId,
                developerName = developerName,
                idToken = idToken
            )
            return true
        } catch (e: Exception) {
            Log.d("updateDeveloperName", "Error updating developer name: ${e.message}")
            return false
        }
    }

    suspend fun updateDeveloperId(idToken: String, postId: String, developerId: String): Boolean {
        try {
            apiClient.updateDeveloperId(
                postId = postId,
                developerId = developerId,
                idToken = idToken
            )
            return true
        } catch (e: Exception) {
            Log.d("updateDeveloperId", "Error updating developer id: ${e.message}")
            return false
        }
    }

    fun getCurrentUser() = currentUser

    suspend fun getFirebaseIdToken(): String {
        return authManager.getFirebaseIdToken()
    }
}