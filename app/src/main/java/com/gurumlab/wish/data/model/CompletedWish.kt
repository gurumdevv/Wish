package com.gurumlab.wish.data.model

data class CompletedWish(
    val postId: Int,
    val createdDate: Int,
    val startedDate: Int,
    val completedDate: Int,
    val posterId: String,
    val developerId: String,
    val posterName: String,
    val developerName: String,
    val title: String,
    val comment: String,
    val repositoryURL: String,
    val accountInfo: String,
    val accountOwner: String,
)