package com.gurumlab.wish.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.source.local.UserDataSource
import com.gurumlab.wish.data.source.remote.ApiClient
import com.gurumlab.wish.data.source.remote.onError
import com.gurumlab.wish.data.source.remote.onException
import com.gurumlab.wish.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val apiClient: ApiClient,
    private val userDataSource: UserDataSource
) {

    fun getUserInfo(): UserInfo? {
        return Firebase.auth.currentUser
    }

    suspend fun logOut() {
        setUid("")
        Firebase.auth.signOut()
    }

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }

    private suspend fun setUid(uid: String) {
        userDataSource.setUid(uid)
    }

    fun deleteAccount(uid: String): Flow<Boolean> = flow {
        val user = Firebase.auth.currentUser!!
        val response = apiClient.getPostsByPosterId(
            orderBy = "\"posterId\"",
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
            Log.d("deleteAccount", "isDeleteWishSuccess: $isDeleteSuccess")
            if (isDeleteSuccess) emit(handleUserDeletion(user))
            else emit(false)
        }.onError { _, _ ->
            emit(handleUserDeletion(user))
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
        setUid("")
        return deleteSuccess
    }

    fun getWishes(
        orderBy: String,
        equalTo: String,
        onError: (message: String?) -> Unit,
        onException: (message: String?) -> Unit
    ): Flow<Map<String, Wish>> = flow {
        val response =
            apiClient.getPostsByDeveloperId("\"${orderBy}\"", "\"${equalTo}\"")
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

    suspend fun deleteWish(
        wishId: String
    ) {
        try {
            apiClient.deleteWish(wishId)
        } catch (e: Exception) {
            Log.d("deleteWish", "Error delete Wish: ${e.message}")
        }
    }
}