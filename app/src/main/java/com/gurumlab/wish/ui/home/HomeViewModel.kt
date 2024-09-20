package com.gurumlab.wish.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.HomeRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _wishes: MutableStateFlow<Map<String, Wish>> = MutableStateFlow(emptyMap())
    val wishes: StateFlow<Map<String, Wish>> = _wishes

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _isException: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isException: StateFlow<Boolean> = _isException

    init {
        loadWishes()
    }

    fun loadWishes() {
        viewModelScope.launch {
            val response = repository.getPostsByDate(
                date = DateTimeConverter.getDateMinusDays(6),
                onCompletion = {
                    _isLoading.value = false
                },
                onSuccess = {
                    _isError.value = false
                    _isException.value = false
                },
                onError = { message ->
                    _isError.value = true
                    _isException.value = false
                    Log.d("HomeViewModel", "onError called $message")
                },
                onException = { message ->
                    _isError.value = false
                    _isException.value = true
                    Log.d("HomeViewModel", "onException called $message")
                }
            )

            response.collect { _wishes.value = it }
        }
    }

    fun getLikes(identifier: String): Flow<Int> = flow {
        val response = repository.getPostsLikes(
            identifier = identifier,
            onError = { message ->
                Log.d("HomeViewModel", "onError called $message")
            },
            onException = { message ->
                Log.d("HomeViewModel", "onException called $message")
            }
        )

        emitAll(response)
    }

    fun updateLikeCount(identifier: String, count: Int) {
        viewModelScope.launch {
            repository.updateLikeCount(identifier, count)
        }
    }
}