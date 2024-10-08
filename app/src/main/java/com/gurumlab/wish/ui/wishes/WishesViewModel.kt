package com.gurumlab.wish.ui.wishes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.WishesRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishesViewModel @Inject constructor(
    private val repository: WishesRepository
) : ViewModel() {

    private val _wishes: MutableStateFlow<Map<String, Wish>> = MutableStateFlow(emptyMap())
    val wishes: StateFlow<Map<String, Wish>> = _wishes

    private val _wishesSortedByLikes: MutableStateFlow<Map<String, Wish>> =
        MutableStateFlow(emptyMap())
    val wishesSortedByLikes: StateFlow<Map<String, Wish>> = _wishesSortedByLikes

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _isException: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isException: StateFlow<Boolean> = _isException

    init {
        _isLoading.value = true
        loadWishes()
        loadWishesSortedByLikes()
    }

    fun loadWishes() {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                _isLoading.value = false
                _isError.value = false
                _isException.value = true
                return@launch
            }

            val response = repository.getPostsByDate(
                idToken = idToken,
                date = DateTimeConverter.getDateMinusDays(10),
                limit = 50,
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
                    Log.d("WishesViewModel", "onError called $message")
                },
                onException = { message ->
                    _isError.value = false
                    _isException.value = true
                    Log.d("WishesViewModel", "onException called $message")
                }
            )

            response.collect { loadedWishes ->
                _wishes.value =
                    loadedWishes.filter { it.value.status != WishStatus.COMPLETED.ordinal }
            }
        }
    }

    fun loadWishesSortedByLikes() {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                _isLoading.value = false
                _isError.value = false
                _isException.value = true
                return@launch
            }

            val response = repository.getPostsByLikes(
                idToken = idToken,
                onSuccess = {
                    _isError.value = false
                    _isException.value = false
                },
                onError = { message ->
                    _isError.value = true
                    _isException.value = false
                    Log.d("WishesViewModel", "onError called $message")
                },
                onException = { message ->
                    _isError.value = false
                    _isException.value = true
                    Log.d("WishesViewModel", "onException called $message")
                }
            )

            response.collect { loadedWishes ->
                _wishesSortedByLikes.value =
                    loadedWishes.filter { it.value.status != WishStatus.COMPLETED.ordinal }
            }
        }
    }
}