package com.gurumlab.wish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var _uid = MutableStateFlow<String?>(null)
    val uid = _uid.asStateFlow()

    fun getUid() {
        viewModelScope.launch {
            _uid.value = repository.getUid()
        }
    }
}