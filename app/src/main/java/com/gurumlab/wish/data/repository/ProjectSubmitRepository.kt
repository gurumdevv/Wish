package com.gurumlab.wish.data.repository

import android.util.Log
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.source.remote.ApiClient
import com.gurumlab.wish.data.source.remote.onError
import com.gurumlab.wish.data.source.remote.onException
import com.gurumlab.wish.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProjectSubmitRepository @Inject constructor(
    private val apiClient: ApiClient
) {

    fun submitWish(
        completedWish: CompletedWish,
        onErrorOrException: (message: String?) -> Unit,
    ): Flow<Map<String, String>> = flow {
        val response = apiClient.uploadCompletedPost(completedWish)
        response.onSuccess {
            emit(it)
        }.onError { code, message ->
            emit(emptyMap())
            onErrorOrException("code: $code, message: $message")
        }.onException {
            emit(emptyMap())
            onErrorOrException(it.message)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateWishStatus(postId: String, status: Int): Boolean {
        try {
            apiClient.updateStatus(postId, status)
            return true
        } catch (e: Exception) {
            Log.d("updateWishStatus", "Error updating wish status: ${e.message}")
            return false
        }
    }

    suspend fun updateCompletedDate(postId: String, completedDate: Int): Boolean{
        try {
            apiClient.updateCompletedDate(postId, completedDate)
            return true
        } catch (e: Exception) {
            Log.d("updateCompletedDate", "Error updating completed date: ${e.message}")
            return false
        }
    }
}