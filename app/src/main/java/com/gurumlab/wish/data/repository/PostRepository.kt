package com.gurumlab.wish.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.source.remote.ApiClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val storageRef: StorageReference,
    private val apiClient: ApiClient,
) {

    suspend fun uploadImages(imageUri: Uri, postId: String, featureIndex: Int): String {
        val imagesRef =
            storageRef.child("images/${postId}/$featureIndex/${imageUri.lastPathSegment}")
        val uploadTask = imagesRef.putFile(imageUri)

        return try {
            uploadTask.await()
            val downloadUrlTask = imagesRef.downloadUrl
            val downloadUri = downloadUrlTask.await()
            downloadUri.toString()
        } catch (e: Exception) {
            Log.e("UploadError", "Image upload failed: ${e.message}")
            ""
        }
    }

    suspend fun uploadPost(wish: Wish): Boolean {
        try {
            apiClient.uploadPost(wish)
            return true
        } catch (e: Exception) {
            Log.d("uploadPost", "Error uploading post: ${e.message}")
            return false
        }
    }

    fun getUid(): String {
        return Firebase.auth.currentUser?.uid ?: ""
    }

    fun getUserName(): String {
        return Firebase.auth.currentUser?.displayName ?: "Wisher"
    }
}