package com.gurumlab.wish.data.model

data class Chat(
    val uid: String = "",
    val message: String = "",
    val sentAt: Long = 0L,
    val submission: Boolean = false //Firebase Database에 "is"는 키워드가 생략되는 버그로 네이밍 컨벤션 예외로 지음
)