package com.gurumlab.wish.ui.detail

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.DetailRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.singleOrNull
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

    val currentDate = DateTimeConverter.getCurrentDate()
    private val currentUser = Firebase.auth.currentUser
    val currentUserUid = currentUser?.uid ?: ""
    val currentUserName = currentUser?.displayName ?: ""

    private val _isStartedDateUpdateSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isStartedDateUpdateSuccess = _isStartedDateUpdateSuccess.asStateFlow()
    private val _isDeveloperNameUpdateSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDeveloperNameUpdateSuccess = _isDeveloperNameUpdateSuccess.asStateFlow()
    private val _isDeveloperIdUpdateSuccess: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDeveloperIdUpdateSuccess = _isDeveloperIdUpdateSuccess.asStateFlow()

    private val _retryCount = mutableStateOf(0)
    val retryCount: State<Int> = _retryCount

    fun initializeDetail(wishId: String) {
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

            response.singleOrNull().let { _wish.value = it }
        }
    }

    fun updateStartedDate(postId: String, startedDate: Int) {
        viewModelScope.launch {
            val response = repository.updateStartedDate(postId, startedDate)
            _isStartedDateUpdateSuccess.value = response
        }
    }

    fun updateDeveloperName(postId: String, developerName: String) {
        viewModelScope.launch {
            val response = repository.updateDeveloperName(postId, developerName)
            _isDeveloperNameUpdateSuccess.value = response
        }
    }

    fun updateDeveloperId(postId: String, developerId: String) {
        viewModelScope.launch {
            val response = repository.updateDeveloperId(postId, developerId)
            _isDeveloperIdUpdateSuccess.value = response
        }
    }

    fun retryLoadWish(wishId: String) {
        _retryCount.value++
        initializeDetail(wishId)
    }
}