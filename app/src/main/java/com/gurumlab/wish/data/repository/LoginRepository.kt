package com.gurumlab.wish.data.repository

import com.gurumlab.wish.data.source.local.UserDataSource
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val userDataSource: UserDataSource
) {

    suspend fun setUid(uid: String) {
        userDataSource.setUid(uid)
    }
}