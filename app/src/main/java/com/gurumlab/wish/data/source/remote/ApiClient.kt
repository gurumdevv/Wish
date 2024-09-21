package com.gurumlab.wish.data.source.remote

import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.model.Wish
import retrofit2.http.Body
import retrofit2.http.DELETE
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
        @Body likeCount: Int
    )

    @PUT("posts/{postId}/status.json")
    suspend fun updateStatus(
        @Path("postId") postId: String,
        @Body status: Int
    )

    @GET("posts/{postIdentifier}/likes.json")
    suspend fun getLikeCount(
        @Path("postIdentifier") postIdentifier: String
    ): ApiResponse<Int>

    @POST("completedPosts.json")
    suspend fun uploadCompletedPost(
        @Body completedWish: CompletedWish
    )

    @GET("posts.json")
    suspend fun getPostsByPosterId(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String
    ): ApiResponse<Map<String, Wish>>

    @GET("posts.json")
    suspend fun getPostsByDeveloperId(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String
    ): ApiResponse<Map<String, Wish>>

    @DELETE("posts/{wishId}.json")
    suspend fun deleteWish(
        @Path("wishId") wishId: String
    )
}