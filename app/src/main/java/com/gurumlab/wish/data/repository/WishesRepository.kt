package com.gurumlab.wish.data.repository

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
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class WishesRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val authManager: FirebaseAuthManager
) {

    fun getPostsByDate(
        idToken: String,
        date: Int,
        limit: Int,
        onCompletion: () -> Unit,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response =
            apiClient.getPostsByDate(
                orderBy = "\"${Constants.CREATED_DATE}\"",
                startAt = date,
                limitToLast = limit,
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

    fun getPostsByLikes(
        idToken: String,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response =
            apiClient.getPostsByLikes(orderBy = "\"likes\"", limitToLast = 10, idToken = idToken)
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
    }.flowOn(Dispatchers.IO)

    suspend fun getFirebaseIdToken(): String {
        return authManager.getFirebaseIdToken()
    }
}