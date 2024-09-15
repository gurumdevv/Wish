package com.gurumlab.wish.data.source.remote

import com.gurumlab.wish.data.model.Wish
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
        likeCount: Int
    )

    @GET("posts.json")
    suspend fun getPostId(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") postId: Int
    ): ApiResponse<Map<String, Wish>>

    @POST("posts.json")
    suspend fun uploadDummyData(
        @Body wish: Wish
    )
}