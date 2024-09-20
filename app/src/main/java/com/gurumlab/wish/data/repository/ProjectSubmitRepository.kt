package com.gurumlab.wish.data.repository

import android.util.Log
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.source.remote.ApiClient
import javax.inject.Inject

class ProjectSubmitRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    suspend fun submitWish(completedWish: CompletedWish): Boolean {
        try {
            apiClient.uploadCompletedPost(completedWish)
            return true
        } catch (e: Exception) {
            Log.d("submitWish", "Error submitting wish: ${e.message}")
            return false
        }
    }

    suspend fun updateWishStatus(postId: String, status: Int): Boolean {
        try {
            apiClient.updateStatus(postId, status)
            return true
        } catch (e: Exception) {
            Log.d("updateWishStatus", "Error updating wish status: ${e.message}")
            return false
        }
    }
}