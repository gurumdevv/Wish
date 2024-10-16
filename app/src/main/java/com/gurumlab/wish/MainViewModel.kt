package com.gurumlab.wish

import androidx.lifecycle.ViewModel
import com.gurumlab.wish.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uid: MutableStateFlow<String?> = MutableStateFlow(null)
    val uid = _uid.asStateFlow()

    init {
        _uid.value = getUid()
    }

    private fun getUid(): String {
        return repository.getCurrentUser()?.uid ?: ""
    }
}