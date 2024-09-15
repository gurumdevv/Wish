package com.gurumlab.wish.data.repository

import android.util.Log
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
    private val apiClient: ApiClient
) {

    fun getPostsByDate(
        date: Int,
        onCompletion: () -> Unit,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response = apiClient.getPostsByDate("\"createdDate\"", date, 30)
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
        identifier: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Int> = flow {
        val response = apiClient.getLikeCount(identifier)
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
        postId: String,
        count: Int
    ) {
        try {
            apiClient.updateLikeCount(postId, count)
        } catch (e: Exception) {
            Log.d("updateLikeCount", "Error updating like count: ${e.message}")
        }
    }
}