package com.gurumlab.wish.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.SettingsRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _accountUiState = MutableStateFlow(AccountUiState())
    val accountUiState = _accountUiState.asStateFlow()

    private val _approachingProjectUiState: MutableStateFlow<ApproachingProjectUiState> =
        MutableStateFlow(ApproachingProjectUiState.Loading)
    val approachingProjectUiState = _approachingProjectUiState.asStateFlow()

    private val _myProjectUiState: MutableStateFlow<MyProjectUiState> =
        MutableStateFlow(MyProjectUiState.Loading)
    val myProjectUiState = _myProjectUiState.asStateFlow()

    val userInfo = fetchUserInfo()

    fun logOut() {
        repository.logOut()
        _accountUiState.update { it.copy(isLogOut = true) }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val idToken = repository.getFirebaseIdToken()

            repository.deleteAccount(idToken = idToken, uid = uid).collect { isSuccess ->
                _accountUiState.update { it.copy(isDeleteAccount = isSuccess) }
            }
        }
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
        }
    }

    private fun fetchUserInfo(): UserInfo {
        val userInfo = repository.getUserInfo()
        val name = userInfo?.displayName ?: Constants.USER
        val email = userInfo?.email ?: Constants.EMAIL
        val imageUrl = userInfo?.photoUrl?.toString() ?: ""
        return UserInfo(name, email, imageUrl)
    }
}

data class UserInfo(
    val name: String,
    val email: String,
    val imageUrl: String
)

data class AccountUiState(
    val isLogOut: Boolean = false,
    val isDeleteAccount: Boolean = false
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