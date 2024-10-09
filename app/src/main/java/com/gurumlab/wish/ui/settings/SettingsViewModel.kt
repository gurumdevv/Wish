package com.gurumlab.wish.ui.settings

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.SettingsRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {

    private var _isLogOut = mutableStateOf(false)
    val isLogout: State<Boolean> = _isLogOut
    private var _isDeleteAccount = mutableStateOf(false)
    val isDeleteAccount: State<Boolean> = _isDeleteAccount

    private val _myWishes: MutableStateFlow<Map<String, Wish>> = MutableStateFlow(emptyMap())
    val myWishes = _myWishes.asStateFlow()
    private val _approachingWishes: MutableStateFlow<Map<String, Wish>> =
        MutableStateFlow(emptyMap())
    val approachingWishes = _approachingWishes.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _isException = MutableStateFlow(false)
    val isException = _isException.asStateFlow()

    fun getUserInfo(): UserInfo {
        val userInfo = repository.getUserInfo()
        val name = userInfo?.displayName ?: Constants.USER
        val email = userInfo?.email ?: Constants.EMAIL
        val imageUrl = userInfo?.photoUrl?.toString() ?: ""
        return UserInfo(name, email, imageUrl)
    }

    fun logOut() {
        repository.logOut()
        _isLogOut.value = true
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val uid = repository.getUid()
            repository.deleteAccount(uid).collect {
                _isDeleteAccount.value = it
            }
        }
    }

    private suspend fun loadWishes(
        orderBy: String,
        onSuccess: (Map<String, Wish>) -> Unit
    ) {
        val uid = repository.getUid()
        val response = repository.getWishes(
            orderBy = orderBy,
            equalTo = uid,
            onError = { e ->
                _isLoading.value = false
                Log.d("SettingsViewModel", "Fail to load data: $e")
            },
            onException = { e ->
                _isLoading.value = false
                _isException.value = true
                Log.d("SettingsViewModel", "Fail to load data: $e")
            }
        )

        response.collect { wishes ->
            onSuccess(wishes)
            _isLoading.value = false
        }
    }

    fun loadApproachingWishes() {
        viewModelScope.launch {
            loadWishes(orderBy = Constants.DEVELOPER_ID) { wishes ->
                _approachingWishes.value = wishes
            }
        }
    }

    fun loadMyWishes() {
        viewModelScope.launch {
            loadWishes(orderBy = Constants.POSTER_ID) { wishes ->
                _myWishes.value = wishes
            }
        }
    }

    suspend fun deleteWish(wishId: String) {
        repository.deleteWish(wishId)
    }

    fun resetUiState() {
        _isLoading.value = true
        _isException.value = false
    }
}

data class UserInfo(
    val name: String,
    val email: String,
    val imageUrl: String
)