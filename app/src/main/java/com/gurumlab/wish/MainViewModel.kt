package com.gurumlab.wish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.auth.FirebaseAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authManager: FirebaseAuthManager
) : ViewModel() {

    private val _idToken = MutableStateFlow<String?>(null)
    val idToken = _idToken.asStateFlow()

    init {
        getIdToken()
    }

    private fun getIdToken() {
        viewModelScope.launch {
            val idToken = authManager.getFirebaseIdToken()
            _idToken.value = idToken
        }
    }
}