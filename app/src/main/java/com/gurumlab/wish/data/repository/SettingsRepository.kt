package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
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
    private val currentUser: FirebaseUser?
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
            var isDeleteSuccess = true
            for (wishId in it.keys) {
                try {
                    apiClient.deleteWish(wishId)
                } catch (e: Exception) {
                    isDeleteSuccess = false
                    break
                }
            }

            if (isDeleteSuccess) emit(handleUserDeletion(currentUser!!))
            else emit(false)
        }.onError { _, _ ->
            emit(handleUserDeletion(currentUser!!))
        }.onException {
            emit(false)
        }
    }

    private suspend fun handleUserDeletion(user: FirebaseUser): Boolean {
        val deleteSuccess = try {
            user.delete().await()
            true
        } catch (e: Exception) {
            false
        }
        return deleteSuccess
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