package com.gurumlab.wish.data.source.remote

import com.gurumlab.wish.data.model.Wish
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    @GET("posts.json")
    suspend fun getPostsByDate(
        @Query("orderBy") orderBy: String,
        @Query("startAt") startAt: Int,
        @Query("limitToLast") limitToLast: Int
    ): ApiResponse<Map<String, Wish>>

    @GET("posts.json")
    suspend fun getPostsByLikes(
        @Query("orderBy") orderBy: String,
        @Query("limitToLast") limitToLast: Int,
    ): ApiResponse<Map<String, Wish>>

    @PUT("posts/{postId}/likes.json")
    suspend fun updateLikeCount(
        @Path("postId") postId: String,
        @Body likeCount: Int
    )

    @GET("posts/{postIdentifier}/likes.json")
    suspend fun getLikeCount(
        @Path("postIdentifier") postIdentifier: String
    ): ApiResponse<Int>
}