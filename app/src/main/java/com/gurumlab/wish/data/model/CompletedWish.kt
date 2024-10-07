package com.gurumlab.wish.data.model

data class CompletedWish(
    val postId: String = "",
    val createdDate: Int = 0,
    val startedDate: Int = 0,
    val completedDate: Int = 0,
    val posterId: String = "",
    val developerId: String = "",
    val posterName: String = "",
    val developerName: String = "",
    val title: String = "",
    val comment: String = "",
    val repositoryURL: String = "",
    val accountInfo: String = "",
    val accountOwner: String = ""
)