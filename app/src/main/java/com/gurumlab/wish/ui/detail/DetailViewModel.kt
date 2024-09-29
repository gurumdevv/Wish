package com.gurumlab.wish.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DetailRepository
) : ViewModel() {

    private val _wish: MutableStateFlow<Wish?> = MutableStateFlow(null)
    val wish = _wish.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _isFailed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFailed = _isFailed.asStateFlow()

    private var _retryCount = 0
    val retryCount = _retryCount

    fun loadWish(wishId: String) {
        viewModelScope.launch {
            val response = repository.getPost(
                postId = wishId,
                onCompletion = {
                    _isLoading.value = false
                },
                onSuccess = {
                    _isFailed.value = false
                },
                onError = { message ->
                    Log.d("DetailViewModel", "onError called $message")
                    _isFailed.value = true
                },
                onException = { message ->
                    Log.d("DetailViewModel", "onException called $message")
                    _isFailed.value = true
                }
            )

            response.collect { _wish.value = it }
        }
    }

    fun retryLoadWish(wishId: String) {
        _retryCount++
        loadWish(wishId)
    }
}