package com.gurumlab.wish.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.ApproachingProjectSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApproachingProjectSettingViewModel @Inject constructor(
    private val repository: ApproachingProjectSettingRepository
) : ViewModel() {

    private val _wishes: MutableStateFlow<Map<String, Wish>> = MutableStateFlow(emptyMap())
    val wishes = _wishes.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _isException = MutableStateFlow(false)
    val isException = _isException.asStateFlow()

    init {
        loadWishes()
    }

    private fun loadWishes() {
        viewModelScope.launch {
            val response = repository.getMyWishes(
                developerId = "developerId",  //TODO("회원 가입 구현 후 아이디 불러오기 구현")
                onError = { e ->
                    _isLoading.value = false
                    Log.d("MyProjectSetting", "Fail to load data: $e")
                },
                onException = { e ->
                    _isLoading.value = false
                    _isException.value = true
                    Log.d("ApproachingProjectSetting", "Fail to load data: $e")
                }
            )

            response.collect { _wishes.value = it }
        }

        _isLoading.value = false
    }
}