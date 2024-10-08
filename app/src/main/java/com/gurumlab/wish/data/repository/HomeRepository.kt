package com.gurumlab.wish.data.repository

import android.util.Log
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
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val authManager: FirebaseAuthManager
) {

    fun getPostsByDate(
        idToken: String,
        date: Int,
        onCompletion: () -> Unit,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response = apiClient.getPostsByDate(
            orderBy = "\"createdDate\"",
            startAt = date,
            limitToLast = 30,
            idToken = idToken
        )
        response.onSuccess {
            emit(it)
            onSuccess()
        }.onError { code, message ->
            emit(emptyMap())
            onError("code: $code, message: $message")
        }.onException {
            emit(emptyMap())
            onException(it.message)
        }
    }.onCompletion {
        onCompletion()
    }.flowOn(Dispatchers.IO)

    fun getPostsLikes(
        idToken: String,
        identifier: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Int> = flow {
        val response = apiClient.getLikeCount(postIdentifier = identifier, idToken = idToken)
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            emit(-1)
            onError("code: $code, message: $message")
        }.onException {
            emit(-1)
            onException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateLikeCount(
        idToken: String,
        postId: String,
        count: Int
    ): Boolean {
        try {
            apiClient.updateLikeCount(postId = postId, likeCount = count, idToken = idToken)
            return true
        } catch (e: Exception) {
            Log.d("updateLikeCount", "Error updating like count: ${e.message}")
            return false
        }
    }

    suspend fun getFirebaseIdToken(): String {
        return authManager.getFirebaseIdToken()
    }
}