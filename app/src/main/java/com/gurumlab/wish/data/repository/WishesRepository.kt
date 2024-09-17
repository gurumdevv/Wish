package com.gurumlab.wish.data.repository

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

class WishesRepository @Inject constructor(
    private val apiClient: ApiClient
) {

    fun getPostsByDate(
        date: Int,
        limit: Int,
        onCompletion: () -> Unit,
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response = apiClient.getPostsByDate("\"createdDate\"", date, limit)
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
        onSuccess: () -> Unit,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response = apiClient.getPostsByLikes("\"likes\"", 10)
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
}