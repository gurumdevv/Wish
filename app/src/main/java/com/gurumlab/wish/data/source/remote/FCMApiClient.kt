package com.gurumlab.wish.data.source.remote

import com.gurumlab.wish.data.model.NotificationRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface FCMApiClient {

    @POST("v1/projects/{projectId}/messages:send")
    suspend fun sendNotification(
        @Path("projectId") name: String,
        @Body request: NotificationRequest
    )
}