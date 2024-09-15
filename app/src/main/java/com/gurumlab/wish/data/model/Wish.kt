package com.gurumlab.wish.data.model

data class Wish(
    val postId: Int,
    val createdDate: Int,
    val startedDate: Int,
    val completedDate: Int,
    val posterId: String,
    val developerId: String,
    val posterName: String,
    val developerName: String,
    val title: String,
    val representativeImage: Int,
    val status: Int,
    val likes: Int,
    val oneLineDescription: String,
    val simpleDescription: String,
    val detailDescription: List<DetailDescription>,
    val features: List<String>,
    val comment: String,
)