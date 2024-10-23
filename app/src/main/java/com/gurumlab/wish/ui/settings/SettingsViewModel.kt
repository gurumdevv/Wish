package com.gurumlab.wish.ui.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.SettingsRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val loginService: LoginService
) : ViewModel() {

    private val _accountUiState = MutableStateFlow(AccountUiState())
    val accountUiState = _accountUiState.asStateFlow()

    private val _approachingProjectUiState: MutableStateFlow<ApproachingProjectUiState> =
        MutableStateFlow(ApproachingProjectUiState.Loading)
    val approachingProjectUiState = _approachingProjectUiState.asStateFlow()

    private val _myProjectUiState: MutableStateFlow<MyProjectUiState> =
        MutableStateFlow(MyProjectUiState.Loading)
    val myProjectUiState = _myProjectUiState.asStateFlow()

    var snackbarMessageRes: MutableState<Int?> = mutableStateOf(null)
        private set

    val userInfo = fetchUserInfo()

    fun logOut() {
        repository.logOut()
        _accountUiState.update { it.copy(isLogOut = true) }
    }

    private fun loadWishes(
        orderBy: String,
        onLoading: () -> Unit,
        onEmpty: () -> Unit,
        onException: (String?) -> Unit,
        onSuccess: (Map<String, Wish>) -> Unit
    ) {
        val uid = repository.getUid()

        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                onException("Fail to get idToken")
                return@launch
            }

            val response = repository.getWishes(
                idToken = idToken,
                orderBy = orderBy,
                equalTo = uid,
                onError = { _ ->
                    onEmpty()
                },
                onException = { e ->
                    onException(e)
                    Log.d("SettingsViewModel", "Fail to load data: $e")
                }
            )

            response
                .onStart { onLoading() }
                .collect { wishes ->
                    onSuccess(wishes)
                }
        }
    }

    fun loadApproachingWishes() {
        loadWishes(
            orderBy = Constants.DEVELOPER_ID,
            onLoading = {
                _approachingProjectUiState.value = ApproachingProjectUiState.Loading
            },
            onEmpty = {
                _approachingProjectUiState.value = ApproachingProjectUiState.Empty
            },
            onException = {
                _approachingProjectUiState.value = ApproachingProjectUiState.Exception
            },
            onSuccess = { wishes ->
                _approachingProjectUiState.value = ApproachingProjectUiState.Success(wishes)
            }
        )
    }

    fun loadMyWishes() {
        loadWishes(
            orderBy = Constants.POSTER_ID,
            onLoading = {
                _myProjectUiState.value = MyProjectUiState.Loading
            },
            onEmpty = {
                _myProjectUiState.value = MyProjectUiState.Empty
            },
            onException = {
                _myProjectUiState.value = MyProjectUiState.Exception
            },
            onSuccess = { wishes ->
                _myProjectUiState.value = MyProjectUiState.Success(wishes)
            }
        )
    }

    suspend fun deleteWish(wishId: String) {
        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                return@launch
            }
            repository.deleteWish(idToken = idToken, wishId = wishId)
            _myProjectUiState.value = MyProjectUiState.Loading
        }
    }

    private fun fetchUserInfo(): CurrentUserInfo {
        val userInfo = repository.getCurrentUser()
        val name = userInfo?.displayName ?: Constants.USER
        val email = userInfo?.email ?: Constants.EMAIL
        val imageUrl = userInfo?.photoUrl?.toString() ?: ""
        return CurrentUserInfo(name, email, imageUrl)
    }

    fun getMinimizedWish(
        wish: Wish,
    ): MinimizedWish {
        return MinimizedWish(
            postId = wish.postId,
            createdDate = wish.createdDate,
            startedDate = wish.startedDate,
            completedDate = wish.completedDate,
            posterId = wish.posterId,
            developerId = wish.developerId,
            posterName = wish.posterName,
            developerName = wish.developerName,
            title = wish.title,
            simpleDescription = wish.simpleDescription,
            comment = wish.comment
        )
    }

    private fun deleteAccount(googleIdToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val uid = repository.getUid()
            val idToken = repository.getFirebaseIdToken()

            if (idToken.isBlank()) {
                _accountUiState.update { it.copy(isLoginFail = true) }
                return@launch
            }

            val wishes = repository.getWishesByUid(
                idToken = idToken,
                uid = uid,
                onException = { e -> Log.d("SettingsViewModel", "Fail to load data: $e") }
            ).single()

            if (wishes == null) {
                _accountUiState.update { it.copy(isDeleteAccount = false) }
                return@launch
            }

            val failWishList = mutableListOf<String>()

            val deleteWishTasks = wishes.map { wish ->
                async {
                    val deleteWishResult =
                        repository.deleteWish(idToken = idToken, wishId = wish.key)
                    val deleteImagesResult = repository.deleteImages(wish.value.postId)

                    if (!deleteWishResult || !deleteImagesResult) {
                        failWishList.add(wish.key)
                    }
                }
            }

            deleteWishTasks.awaitAll()

            if (failWishList.isEmpty()) {
                val isDeleteAccount =
                    repository.removeFirebaseAuthUser(googleIdToken, repository.getCurrentUser()!!)
                _accountUiState.update { it.copy(isDeleteAccount = isDeleteAccount) }
            } else {
                _accountUiState.update { it.copy(isDeleteAccount = false) }
            }
        }
    }

    fun signInWithGoogle(data: Intent?) {
        loginService.signInWithGoogle(data)
    }

    fun setSignInRequest(
        oneTapSignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
        legacySignInLauncher: ActivityResultLauncher<IntentSenderRequest>,
        context: Context
    ) {
        loginService.setSignInRequest(oneTapSignInLauncher, legacySignInLauncher, context)

        viewModelScope.launch(Dispatchers.IO) {
            loginService.idToken.collect { idToken ->
                idToken?.run {
                    if (isNotBlank()) {
                        deleteAccount(this)
                    } else {
                        _accountUiState.update { it.copy(isLoginFail = true) }
                    }
                }
            }
        }
    }

    fun showSnackbarMessage(messageRes: Int) {
        viewModelScope.launch {
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

    fun resetIsDeleteAccount() {
        _accountUiState.update { it.copy(isDeleteAccount = null) }
    }

    fun resetIsLoginFail() {
        _accountUiState.update { it.copy(isLoginFail = false) }
    }
}

data class CurrentUserInfo(
    val name: String,
    val email: String,
    val imageUrl: String
)

data class AccountUiState(
    val isLogOut: Boolean = false,
    val isDeleteAccount: Boolean? = null,
    val isLoginFail: Boolean = false
)

sealed class ApproachingProjectUiState {
    data class Success(val wishes: Map<String, Wish>) : ApproachingProjectUiState()
    data object Loading : ApproachingProjectUiState()
    data object Empty : ApproachingProjectUiState()
    data object Exception : ApproachingProjectUiState()
}

sealed class MyProjectUiState {
    data class Success(val wishes: Map<String, Wish>) : MyProjectUiState()
    data object Loading : MyProjectUiState()
    data object Empty : MyProjectUiState()
    data object Exception : MyProjectUiState()
}