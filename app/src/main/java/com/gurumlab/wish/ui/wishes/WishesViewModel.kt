package com.gurumlab.wish.ui.wishes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.WishesRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import com.gurumlab.wish.ui.util.NumericConstants
import com.gurumlab.wish.ui.util.WishesUpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishesViewModel @Inject constructor(
    private val repository: WishesRepository,
    private val wishesUpdateManager: WishesUpdateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishesUiState())
    val uiState: StateFlow<WishesUiState> = _uiState.asStateFlow()

    init {
        loadWishes()
        loadWishesSortedByLikes()
        observeWishesChanges()
    }

    fun loadWishes() {
        _uiState.update { it.copy(isWishesLoading = true) }
        loadWishesData(
            isSortedByLikes = false,
            isLoadingUpdate = { isLoading -> _uiState.value.copy(isWishesLoading = isLoading) },
            wishesUpdate = { wishes -> _uiState.value.copy(wishes = wishes) }
        )
    }

    fun loadWishesSortedByLikes() {
        _uiState.update { it.copy(isWishesSortedByLikesLoading = true) }
        loadWishesData(
            isSortedByLikes = true,
            isLoadingUpdate = { isLoading -> _uiState.value.copy(isWishesSortedByLikesLoading = isLoading) },
            wishesUpdate = { wishes -> _uiState.value.copy(wishesSortedByLikes = wishes) }
        )
    }

    private fun loadWishesData(
        isSortedByLikes: Boolean,
        isLoadingUpdate: (Boolean) -> WishesUiState,
        wishesUpdate: (Map<String, Wish>) -> WishesUiState
    ) {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                _uiState.update {
                    it.copy(
                        isWishesLoading = false,
                        isError = false,
                        isException = true
                    )
                }
                return@launch
            }

            val onSuccess = {
                _uiState.update { it.copy(isError = false, isException = false) }
            }
            val onError: (message: String?) -> Unit = { message ->
                _uiState.update { it.copy(isError = true, isException = false) }
                Log.d("WishesViewModel", "onError called $message")
            }
            val onException: (message: String?) -> Unit = { message ->
                _uiState.update { it.copy(isError = false, isException = true) }
                Log.d("WishesViewModel", "onException called $message")
            }

            val response = if (isSortedByLikes) {
                repository.getPostsByLikes(
                    idToken = idToken,
                    onCompletion = { _uiState.update { isLoadingUpdate(false) } },
                    onSuccess = onSuccess,
                    onError = onError,
                    onException = onException
                )
            } else {
                repository.getPostsByDate(
                    idToken = idToken,
                    date = DateTimeConverter.getDateMinusDays(NumericConstants.DEFAULT_POST_LOAD_DAY_LIMIT),
                    limit = NumericConstants.DEFAULT_POST_LOAD_COUNT_LIMIT,
                    onCompletion = { _uiState.update { isLoadingUpdate(false) } },
                    onSuccess = onSuccess,
                    onError = onError,
                    onException = onException
                )
            }

            response.collect { loadedWishes ->
                val filteredWishes =
                    loadedWishes.filter { it.value.status == WishStatus.POSTED.ordinal }
                _uiState.update { wishesUpdate(filteredWishes) }
            }
        }
    }

    private fun observeWishesChanges() {
        viewModelScope.launch {
            wishesUpdateManager.count.drop(1).collect { _ ->
                loadWishes()
                loadWishesSortedByLikes()
            }
        }
    }
}

data class WishesUiState(
    val wishes: Map<String, Wish> = emptyMap(),
    val wishesSortedByLikes: Map<String, Wish> = emptyMap(),
    val isWishesLoading: Boolean = true,
    val isWishesSortedByLikesLoading: Boolean = true,
    val isError: Boolean = false,
    val isException: Boolean = false
)