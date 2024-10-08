package com.gurumlab.wish.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.HomeRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishesUiState())
    val uiState: StateFlow<WishesUiState> = _uiState

    init {
        _uiState.update { it.copy(isLoading = true) }
        loadWishes()
    }

    fun loadWishes() {
        viewModelScope.launch {
            val idToken = getIdTokenOrHandleError()
            if (idToken.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, isError = false, isException = true) }
                return@launch
            }

            val response = repository.getPostsByDate(
                idToken = idToken,
                date = DateTimeConverter.getDateMinusDays(6),
                onCompletion = {
                    _uiState.update { it.copy(isLoading = false) }
                },
                onSuccess = {
                    _uiState.update { it.copy(isError = false, isException = false) }
                },
                onError = { message ->
                    _uiState.update { it.copy(isError = true, isException = false) }
                    Log.d("HomeViewModel", "onError called $message")
                },
                onException = { message ->
                    _uiState.update { it.copy(isError = false, isException = true) }
                    Log.d("HomeViewModel", "onException called $message")
                }
            )

            response.collect { loadedWishes ->
                val filteredWishes =
                    loadedWishes.filter { it.value.status != WishStatus.COMPLETED.ordinal }
                _uiState.update {
                    it.copy(
                        wishes = filteredWishes,
                        isEmpty = filteredWishes.isEmpty()
                    )
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
                _uiState.update { it.copy(isFailUpdateLikeCount = true) }
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
        _uiState.update { it.copy(isFailUpdateLikeCount = false) }
    }

    private suspend fun getIdTokenOrHandleError(): String? {
        val idToken = repository.getFirebaseIdToken()
        if (idToken.isBlank()) {
            return null
        }
        return idToken
    }
}

data class WishesUiState(
    val wishes: Map<String, Wish> = emptyMap(),
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val isException: Boolean = false,
    val isEmpty: Boolean = false,
    val isFailUpdateLikeCount: Boolean = false
)