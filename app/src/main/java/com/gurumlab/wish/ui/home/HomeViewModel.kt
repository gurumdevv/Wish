package com.gurumlab.wish.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.HomeRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import com.gurumlab.wish.ui.util.NumericConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isFailUpdateLikeCount: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isFailUpdateLikeCount: StateFlow<Boolean> = _isFailUpdateLikeCount.asStateFlow()

    fun loadWishes() {
        viewModelScope.launch {
            val idToken = getIdTokenOrHandleError()
            if (idToken.isNullOrBlank()) {
                _uiState.value = HomeUiState.Exception
                return@launch
            }

            val response = repository.getPostsByDate(
                idToken = idToken,
                date = DateTimeConverter.getDateMinusDays(NumericConstants.DEFAULT_POST_LOAD_DAY_LIMIT),
                onError = { message ->
                    _uiState.value = HomeUiState.Empty
                },
                onException = { message ->
                    _uiState.value = HomeUiState.Exception
                    Log.d("HomeViewModel", "onException called $message")
                }
            )

            response
                .onStart { _uiState.value = HomeUiState.Loading }
                .collect { loadedWishes ->
                    val filteredWishes =
                        loadedWishes.filter { it.value.status == WishStatus.POSTED.ordinal }
                    if (filteredWishes.isEmpty()) {
                        _uiState.value = HomeUiState.Empty
                    } else {
                        _uiState.value = HomeUiState.Success(filteredWishes)
                    }
                }
        }
    }

    fun getLikes(identifier: String): Flow<Int> = flow {
        val idToken = getIdTokenOrHandleError() ?: return@flow emit(-1)

        val response = repository.getPostsLikes(
            idToken = idToken,
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
            val isSuccess = performUpdateLikeCount(identifier, count)
            if (!isSuccess) {
                _isFailUpdateLikeCount.value = true
                resetIsFailUpdateLikeCount()
            }
        }
    }

    private suspend fun performUpdateLikeCount(identifier: String, count: Int): Boolean {
        val idToken = repository.getFirebaseIdToken()
        if (idToken.isBlank()) {
            return false
        }
        return repository.updateLikeCount(idToken, identifier, count)
    }

    private fun resetIsFailUpdateLikeCount() {
        _isFailUpdateLikeCount.value = false
    }

    private suspend fun getIdTokenOrHandleError(): String? {
        val idToken = repository.getFirebaseIdToken()
        if (idToken.isBlank()) {
            return null
        }
        return idToken
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data object Empty : HomeUiState()
    data object Exception : HomeUiState()
    data class Success(val wishes: Map<String, Wish>) : HomeUiState()
}