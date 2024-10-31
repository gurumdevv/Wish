package com.gurumlab.wish.data.model

data class UserInfo(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val fcmToken: String = ""
)