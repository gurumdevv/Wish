package com.gurumlab.wish.data.repository

import com.gurumlab.wish.data.source.local.UserDataSource
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val userDataSource: UserDataSource
) {

    suspend fun getUid(): String {
        return userDataSource.getUid()
    }
}