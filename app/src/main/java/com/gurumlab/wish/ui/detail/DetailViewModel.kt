package com.gurumlab.wish.ui.detail

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.DetailRepository
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DetailRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<DetailUiState> = MutableStateFlow(DetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _startingWishUiState: MutableStateFlow<StartingWishUiState> =
        MutableStateFlow(StartingWishUiState())
    val startingWishUiState = _startingWishUiState.asStateFlow()

    var snackbarMessageRes: MutableState<Int?> = mutableStateOf(null)
        private set

    fun loadWish(wishId: String) {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                _uiState.value = DetailUiState.Fail
                return@launch
            }

            val response = repository.getPost(
                idToken = idToken,
                postId = wishId,
                onError = { message ->
                    Log.d("DetailViewModel", "onError called $message")
                    _uiState.value = DetailUiState.Fail
                },
                onException = { message ->
                    Log.d("DetailViewModel", "onException called $message")
                    _uiState.value = DetailUiState.Fail
                }
            )

            _uiState.value = response
                .onStart { _uiState.value = DetailUiState.Loading }
                .singleOrNull()?.let {
                    DetailUiState.Success(it)
                } ?: DetailUiState.Fail
        }
    }

    private suspend fun updateStatus(idToken: String, postId: String): Boolean {
        val response = repository.updateStatus(
            idToken = idToken,
            postId = postId,
            status = WishStatus.ONGOING.ordinal
        )
        return if (response) {
            _startingWishUiState.update { it.copy(isStatusUpdateSuccess = true) }
            true
        } else false
    }

    private suspend fun updateStartedDate(
        idToken: String,
        postId: String,
        startedDate: Int
    ): Boolean {
        val response = repository.updateStartedDate(
            idToken = idToken,
            postId = postId,
            startedDate = startedDate
        )
        return if (response) {
            _startingWishUiState.update { it.copy(isStartedDateUpdateSuccess = true) }
            true
        } else false
    }

    private suspend fun updateDeveloperName(
        idToken: String,
        postId: String,
        developerName: String
    ): Boolean {
        val response = repository.updateDeveloperName(
            idToken = idToken,
            postId = postId,
            developerName = developerName
        )
        return if (response) {
            _startingWishUiState.update { it.copy(isDeveloperNameUpdateSuccess = true) }
            true
        } else false
    }

    private suspend fun updateDeveloperId(
        idToken: String,
        postId: String,
        developerId: String
    ): Boolean {
        val response = repository.updateDeveloperId(
            idToken = idToken,
            postId = postId,
            developerId = developerId
        )
        return if (response) {
            _startingWishUiState.update { it.copy(isDeveloperIdUpdateSuccess = true) }
            true
        } else false
    }

    fun fetchCurrentDate(): Int = DateTimeConverter.getCurrentDate()

    fun fetchUserInfo(): UserInfo {
        val currentUser = repository.getCurrentUser()
        return UserInfo(
            uid = currentUser?.uid ?: "",
            name = currentUser?.displayName ?: ""
        )
    }

    fun fetchMinimizedWish(
        wish: Wish,
        currentDate: Int,
        currentUser: UserInfo
    ): MinimizedWish {
        return MinimizedWish(
            postId = wish.postId,
            createdDate = wish.createdDate,
            startedDate = currentDate,
            completedDate = wish.completedDate,
            posterId = wish.posterId,
            developerId = currentUser.uid,
            posterName = wish.posterName,
            developerName = currentUser.name,
            title = wish.title,
            simpleDescription = wish.simpleDescription,
            comment = wish.comment
        )
    }

    fun updateWish(
        wishId: String,
        currentDate: Int,
        currentUser: UserInfo,
        alreadyBegunMessageRes: Int,
        failSnackbarMessageRes: Int
    ) {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                handleError(logMessage = "IdToken is blank", messageRes = failSnackbarMessageRes)
                return@launch
            }

            if (isLatestStatusAlreadyBegun(
                    idToken = idToken,
                    wishId = wishId,
                    failSnackbarMessageRes = failSnackbarMessageRes
                )
            ) {
                handleError(logMessage = "", messageRes = alreadyBegunMessageRes)
                return@launch
            }

            val isStatusUpdatedSuccess = async { updateStatus(idToken, wishId) }.await()
            val isDateUpdatedSuccess =
                async { updateStartedDate(idToken, wishId, currentDate) }.await()
            val isDeveloperNameUpdatedSuccess =
                async { updateDeveloperName(idToken, wishId, currentUser.name) }.await()
            val isDeveloperIdUpdatedSuccess =
                async { updateDeveloperId(idToken, wishId, currentUser.uid) }.await()

            if (!isStatusUpdatedSuccess ||
                !isDateUpdatedSuccess ||
                !isDeveloperNameUpdatedSuccess ||
                !isDeveloperIdUpdatedSuccess
            ) {
                handleError(
                    logMessage = "Updating wish failed",
                    messageRes = failSnackbarMessageRes
                )
            }
        }
    }

    private suspend fun isLatestStatusAlreadyBegun(
        idToken: String,
        wishId: String,
        failSnackbarMessageRes: Int
    ): Boolean {
        val currentStatus = repository.getPostStatus(
            idToken = idToken,
            postId = wishId,
            onFail = { message ->
                handleError(
                    logMessage = "Getting status failed: $message",
                    messageRes = failSnackbarMessageRes
                )
            }
        ).singleOrNull()

        return currentStatus != WishStatus.POSTED.ordinal
    }

    private fun handleError(logMessage: String, messageRes: Int) {
        viewModelScope.launch {
            Log.d("DetailViewModel", logMessage)
            updateSnackbarMessage(messageRes)
            delay(4000L) //SnackbarDuration.Short
            resetSnackbarMessage()
        }
    }

    private fun updateSnackbarMessage(messageRes: Int) {
        snackbarMessageRes.value = messageRes
    }

    private fun resetSnackbarMessage() {
        snackbarMessageRes.value = null
    }
}

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data object Fail : DetailUiState()
    data class Success(val wish: Wish) : DetailUiState()
}

data class StartingWishUiState(
    val isStatusUpdateSuccess: Boolean = false,
    val isStartedDateUpdateSuccess: Boolean = false,
    val isDeveloperNameUpdateSuccess: Boolean = false,
    val isDeveloperIdUpdateSuccess: Boolean = false,
)