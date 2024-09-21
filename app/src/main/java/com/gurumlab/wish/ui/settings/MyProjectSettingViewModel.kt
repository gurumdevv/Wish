package com.gurumlab.wish.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.repository.MyProjectSettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyProjectSettingViewModel @Inject constructor(
    private val repository: MyProjectSettingRepository
) : ViewModel() {

    val wishes: StateFlow<Map<String, Wish>> = loadWishes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap()
    )
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private fun loadWishes(): Flow<Map<String, Wish>> = flow {
        val response = repository.getMyWishes(

            posterId = "creatorId",  //TODO("회원 가입 구현 후 아이디 불러오기 구현")
            onError = { e ->
                _isLoading.value = false
                Log.d("MyProjectSetting", "Fail to load data: $e")
            },
            onException = { e ->
                _isLoading.value = false
                Log.d("MyProjectSetting", "Fail to load data: $e")
            }
        )

        _isLoading.value = false
        emitAll(response)
    }
}