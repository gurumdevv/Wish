package com.gurumlab.wish.ui.projectSubmit

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

    private val _isSubmitSuccess = MutableStateFlow(false)
    val isSubmitSuccess = _isSubmitSuccess.asStateFlow()
    private val _isUpdateSuccess = MutableStateFlow(false)
    val isUpdateSuccess = _isUpdateSuccess.asStateFlow()

    fun submitWish(
        minimizedWish: MinimizedWish,
        repositoryURL: String,
        accountInfo: String,
        accountOwner: String
    ) {
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
            repositoryURL = repositoryURL,
            accountInfo = accountInfo,
            accountOwner = accountOwner
        )

        viewModelScope.launch {
            _isSubmitSuccess.value = repository.submitWish(completedWish)
        }
    }

    fun updateWishStatusToComplete(postId: String) {
        viewModelScope.launch {
            _isUpdateSuccess.value = repository.updateWishStatus(postId, 2)
        }
    }
}