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

class DetailRepository @Inject constructor(
    private val apiClient: ApiClient
) {

    fun getPost(
        postId: String,
        onCompletion: () -> Unit,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Wish?> = flow {
        val response = apiClient.getPostByPostId(postId)
        response.onSuccess {
            emit(it)
            onSuccess()
        }.onError { code, message ->
            emit(null)
            onError("code: $code, message: $message")
        }.onException {
            emit(null)
            onException(it.message)
        }
    }.onCompletion {
        onCompletion()
    }.flowOn(Dispatchers.IO)


    suspend fun updateStartedDate(postId: String, startedDate: Int): Boolean {
        try {
            apiClient.updateStartedDate(postId, startedDate)
            return true
        } catch (e: Exception) {
            Log.d("updateStartedDate", "Error updating started date: ${e.message}")
            return false
        }
    }

    suspend fun updateDeveloperName(postId: String, developerName: String): Boolean {
        try {
            apiClient.updateDeveloperName(postId, developerName)
            return true
        } catch (e: Exception) {
            Log.d("updateDeveloperName", "Error updating developer name: ${e.message}")
            return false
        }
    }

    suspend fun updateDeveloperId(postId: String, developerId: String): Boolean {
        try {
            apiClient.updateDeveloperId(postId, developerId)
            return true
        } catch (e: Exception) {
            Log.d("updateDeveloperId", "Error updating developer id: ${e.message}")
            return false
        }
    }
}