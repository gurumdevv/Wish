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

    @GET("posts/{postId}.json")
    suspend fun getPostByPostId(
        @Path("postId") postId: String,
        @Query("auth") idToken: String
    ): ApiResponse<Wish>

    @GET("posts.json")
    suspend fun getPostsByDate(
        @Query("orderBy") orderBy: String,
        @Query("startAt") startAt: Int,
        @Query("limitToLast") limitToLast: Int,
        @Query("auth") idToken: String
    ): ApiResponse<Map<String, Wish>>

    @GET("posts.json")
    suspend fun getPostsByLikes(
        @Query("orderBy") orderBy: String,
        @Query("limitToLast") limitToLast: Int,
        @Query("auth") idToken: String
    ): ApiResponse<Map<String, Wish>>

    @PUT("posts/{postId}/likes.json")
    suspend fun updateLikeCount(
        @Path("postId") postId: String,
        @Body likeCount: Int,
        @Query("auth") idToken: String
    )

    @PUT("posts/{postId}/status.json")
    suspend fun updateStatus(
        @Path("postId") postId: String,
        @Body status: Int,
        @Query("auth") idToken: String
    )

    @PUT("posts/{postId}/completedDate.json")
    suspend fun updateCompletedDate(
        @Path("postId") postId: String,
        @Body completedDate: Int,
        @Query("auth") idToken: String
    )

    @PUT("posts/{postId}/startedDate.json")
    suspend fun updateStartedDate(
        @Path("postId") postId: String,
        @Body startedDate: Int,
        @Query("auth") idToken: String
    )

    @PUT("posts/{postId}/developerId.json")
    suspend fun updateDeveloperId(
        @Path("postId") postId: String,
        @Body developerId: String,
        @Query("auth") idToken: String
    )

    @PUT("posts/{postId}/developerName.json")
    suspend fun updateDeveloperName(
        @Path("postId") postId: String,
        @Body developerName: String,
        @Query("auth") idToken: String
    )

    @GET("posts/{postIdentifier}/likes.json")
    suspend fun getLikeCount(
        @Path("postIdentifier") postIdentifier: String,
        @Query("auth") idToken: String
    ): ApiResponse<Int>

    @POST("posts.json")
    suspend fun uploadPost(
        @Body wish: Wish
    )

    @POST("completedPosts.json")
    suspend fun uploadCompletedPost(
        @Body completedWish: CompletedWish,
        @Query("auth") idToken: String
    ): ApiResponse<Map<String, String>>

    @GET("posts.json")
    suspend fun getPostsByUid(
        @Query("orderBy") orderBy: String,
        @Query("equalTo") equalTo: String,
        @Query("auth") idToken: String
    ): ApiResponse<Map<String, Wish>>

    @DELETE("posts/{wishId}.json")
    suspend fun deleteWish(
        @Path("wishId") wishId: String,
        @Query("auth") idToken: String
    )
}