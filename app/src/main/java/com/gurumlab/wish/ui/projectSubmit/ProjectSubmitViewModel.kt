package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.repository.ProjectSubmitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectSubmitViewModel @Inject constructor(
    private val repository: ProjectSubmitRepository
) : ViewModel() {

    private val _repositoryInfo = mutableStateOf("")
    val repositoryInfo: State<String> = _repositoryInfo
    private val _accountInfo = mutableStateOf("")
    val accountInfo: State<String> = _accountInfo
    private val _accountOwner = mutableStateOf("")
    val accountOwner: State<String> = _accountOwner
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _snackbarMessage: MutableState<Int?> = mutableStateOf(null)
    val snackbarMessage: State<Int?> = _snackbarMessage

    private val _isSubmitSuccess = MutableStateFlow(false)
    val isSubmitSuccess = _isSubmitSuccess.asStateFlow()
    private val _isUpdateSuccess = MutableStateFlow(false)
    val isUpdateSuccess = _isUpdateSuccess.asStateFlow()

    fun submitWish(
        wishId: String,
        minimizedWish: MinimizedWish,
        snackbarMessageRes: Int
    ) {
        if (repositoryInfo.value.isNotBlank() && accountInfo.value.isNotBlank() && accountOwner.value.isNotBlank()) {
            _isLoading.value = true

            val completedWish = CompletedWish(
                postId = minimizedWish.postId,
                createdDate = minimizedWish.createdDate,
                startedDate = minimizedWish.startedDate,
                completedDate = minimizedWish.completedDate,
                posterId = minimizedWish.posterId,
                developerId = minimizedWish.developerId,
                posterName = minimizedWish.posterName,
                developerName = minimizedWish.developerName,
                title = minimizedWish.title,
                comment = minimizedWish.comment,
                repositoryURL = repositoryInfo.value,
                accountInfo = accountInfo.value,
                accountOwner = accountOwner.value
            )

            viewModelScope.launch {
                _isSubmitSuccess.value = repository.submitWish(completedWish)
                _isUpdateSuccess.value = repository.updateWishStatus(wishId, 2)
            }
        } else {
            _snackbarMessage.value = snackbarMessageRes
        }
    }

    fun resetSnackbarMessageState() {
        _snackbarMessage.value = null
    }

    fun setRepositoryInfo(info: String) {
        _repositoryInfo.value = info
    }

    fun setAccountInfo(info: String) {
        _accountInfo.value = info
    }

    fun setAccountOwner(owner: String) {
        _accountOwner.value = owner
    }
}