package com.gurumlab.wish.ui.message

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.CompletedWish
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SubmissionViewModel @Inject constructor() : ViewModel() {

    private val _completedWish = MutableStateFlow<CompletedWish?>(null)
    val completedWish: StateFlow<CompletedWish?> = _completedWish

    fun initializeData(completedWishId: String) {
        val database = Firebase.database
        val completedWishRef = database.getReference("completedPosts").child(completedWishId)
        completedWishRef.get().addOnSuccessListener { snapshot ->
            val completedWish = snapshot.getValue(CompletedWish::class.java)
            _completedWish.value = completedWish
        }
    }
}