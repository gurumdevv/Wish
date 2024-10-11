package com.gurumlab.wish.ui.projectSubmit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.ProjectSubmitRepository
import com.gurumlab.wish.ui.util.Constants
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProjectSubmitViewModel @Inject constructor(
    private val repository: ProjectSubmitRepository
) : ViewModel() {

    private val _projectSubmitInputFieldUiState = mutableStateOf(ProjectSubmitInputFieldUiState())
    val projectSubmitInputFieldUiState: State<ProjectSubmitInputFieldUiState> get() = _projectSubmitInputFieldUiState

    private val _projectUpdateUiState = MutableStateFlow(ProjectUpdateUiState())
    val projectUpdateUiState = _projectUpdateUiState.asStateFlow()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    private val _snackbarMessage: MutableState<Int?> = mutableStateOf(null)
    val snackbarMessage: State<Int?> = _snackbarMessage

    fun onProjectSubmitInputFieldChange(
        repositoryInfo: String? = null,
        accountInfo: String? = null,
        accountOwner: String? = null
    ) {
        _projectSubmitInputFieldUiState.value = _projectSubmitInputFieldUiState.value.copy(
            repositoryInfo = repositoryInfo ?: _projectSubmitInputFieldUiState.value.repositoryInfo,
            accountInfo = accountInfo ?: _projectSubmitInputFieldUiState.value.accountInfo,
            accountOwner = accountOwner ?: _projectSubmitInputFieldUiState.value.accountOwner
        )
    }

    fun submitWish(
        wishId: String,
        minimizedWish: MinimizedWish,
        emptySnackbarMessageRes: Int,
        failSnackbarMessageRes: Int,
        projectCompletedDescription: String
    ) {
        if (!isInputFieldsValid()) {
            _snackbarMessage.value = emptySnackbarMessageRes
            return
        }

        _isLoading.value = true

        val completedWish = fetchCompletedWish(
            minimizedWish = minimizedWish,
            inputFieldUiState = _projectSubmitInputFieldUiState.value,
            currentDate = DateTimeConverter.getCurrentDate()
        )

        viewModelScope.launch {
            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                Log.d("ProjectSubmitViewModel", "idToken is blank")
                updateFailState(failSnackbarMessageRes)
                return@launch
            }

            val completedWishId = repository.submitWish(
                idToken = idToken,
                completedWish = completedWish,
                onErrorOrException = {
                    Log.d("ProjectSubmitViewModel", "upload completedWish failed")
                    updateFailState(failSnackbarMessageRes)
                },
            ).single()

            val isMessageSent = sendMessageWithSubmission(
                completedWishId = completedWishId.values.first(),
                minimizedWish = minimizedWish,
                projectCompletedDescription = projectCompletedDescription
            )

            if (isMessageSent) {
                _projectUpdateUiState.update { it.copy(isSubmitSuccess = true) }

                updateProjectStatus(
                    idToken = idToken,
                    wishId = wishId,
                    failSnackbarMessageRes = failSnackbarMessageRes
                )
            } else {
                updateFailState(failSnackbarMessageRes)
            }
        }
    }

    private fun updateProjectStatus(idToken: String, wishId: String, failSnackbarMessageRes: Int) {
        viewModelScope.launch {
            val isStatusUpdatedSuccess = async {
                updateCompletedDate(
                    idToken = idToken,
                    wishId = wishId
                )
            }.await()
            val isCompletedDateUpdatedSuccess = async {
                updateWishStatus(
                    idToken = idToken,
                    wishId = wishId
                )
            }.await()

            if (!isStatusUpdatedSuccess || !isCompletedDateUpdatedSuccess) {
                Log.d("ProjectSubmitViewModel", "Status or Date Update failed")
                updateFailState(failSnackbarMessageRes)
            }
        }
    }

    private fun isInputFieldsValid(): Boolean {
        val state = _projectSubmitInputFieldUiState.value
        return state.repositoryInfo.isNotBlank() &&
                state.accountInfo.isNotBlank() &&
                state.accountOwner.isNotBlank()
    }

    private suspend fun sendMessageWithSubmission(
        completedWishId: String,
        minimizedWish: MinimizedWish,
        projectCompletedDescription: String
    ): Boolean {
        return try {
            val currentUser = repository.getCurrentUser()
            val uid = currentUser?.uid ?: ""
            val othersUid = minimizedWish.posterId
            val roomId = "${uid}+${othersUid}"

            val fireStore = repository.getFireStore()
            val myFireStoreRef = fireStore.collection(uid).document(roomId)
            val othersFireStoreRef = fireStore.collection(othersUid).document(roomId)
            val batch = fireStore.batch()

            val database = repository.getDatabaseRef()
            val messagesRef = database.child(Constants.MESSAGES).child(roomId)

            val newChat = Chat(
                uid = uid,
                message = completedWishId,
                sentAt = System.currentTimeMillis(),
                submission = true
            )

            val currentTimeStamp = FieldValue.serverTimestamp()
            val chatRoomData = mutableMapOf(
                Constants.LAST_MESSAGE to projectCompletedDescription,
                Constants.LAST_SENT_AT to currentTimeStamp
            )

            messagesRef.push().setValue(newChat).await()

            val chatRoomSnapshot = myFireStoreRef.get().await()
            if (chatRoomSnapshot.exists()) {
                batch.update(myFireStoreRef, chatRoomData)
                batch.update(othersFireStoreRef, chatRoomData)
                batch.update(
                    othersFireStoreRef,
                    Constants.NOT_READ_MESSAGE_COUNT,
                    FieldValue.increment(1)
                )
                batch.commit().await()
            } else {
                val myChatRoomData = mutableMapOf(
                    Constants.ID to roomId,
                    Constants.OTHERS_UID to othersUid,
                    Constants.LAST_MESSAGE to projectCompletedDescription,
                    Constants.LAST_SENT_AT to currentTimeStamp,
                    Constants.NOT_READ_MESSAGE_COUNT to 0
                )
                val othersChatRoomData = mutableMapOf(
                    Constants.ID to roomId,
                    Constants.OTHERS_UID to uid,
                    Constants.LAST_MESSAGE to projectCompletedDescription,
                    Constants.LAST_SENT_AT to currentTimeStamp,
                    Constants.NOT_READ_MESSAGE_COUNT to 1
                )

                batch.set(myFireStoreRef, myChatRoomData)
                batch.set(othersFireStoreRef, othersChatRoomData)
                batch.commit().await()
            }

            true
        } catch (e: Exception) {
            Log.d("ProjectSubmitViewModel", "Message submission failed: ${e.message}")
            false
        }
    }

    private suspend fun updateWishStatus(idToken: String, wishId: String): Boolean {
        val result = repository.updateWishStatus(
            idToken = idToken,
            postId = wishId,
            status = WishStatus.COMPLETED.ordinal
        )
        _projectUpdateUiState.update { it.copy(isStatusUpdateSuccess = result) }
        return result
    }

    private suspend fun updateCompletedDate(idToken: String, wishId: String): Boolean {
        val currentDate = DateTimeConverter.getCurrentDate()
        val result = repository.updateCompletedDate(
            idToken = idToken,
            postId = wishId,
            completedDate = currentDate
        )
        _projectUpdateUiState.update { it.copy(isCompletedDateUpdateSuccess = result) }
        return result
    }

    private fun updateFailState(failSnackbarMessageRes: Int) {
        _isLoading.value = false
        _snackbarMessage.value = failSnackbarMessageRes
        resetSnackbarMessageState()
    }

    private fun resetSnackbarMessageState() {
        _snackbarMessage.value = null
    }

    private fun fetchCompletedWish(
        minimizedWish: MinimizedWish,
        inputFieldUiState: ProjectSubmitInputFieldUiState,
        currentDate: Int
    ): CompletedWish {
        return CompletedWish(
            postId = minimizedWish.postId,
            createdDate = minimizedWish.createdDate,
            startedDate = minimizedWish.startedDate,
            completedDate = currentDate,
            posterId = minimizedWish.posterId,
            developerId = minimizedWish.developerId,
            posterName = minimizedWish.posterName,
            developerName = minimizedWish.developerName,
            title = minimizedWish.title,
            comment = minimizedWish.comment,
            repositoryURL = inputFieldUiState.repositoryInfo,
            accountInfo = inputFieldUiState.accountInfo,
            accountOwner = inputFieldUiState.accountOwner
        )
    }
}

data class ProjectUpdateUiState(
    val isSubmitSuccess: Boolean = false,
    val isStatusUpdateSuccess: Boolean = false,
    val isCompletedDateUpdateSuccess: Boolean = false
)

data class ProjectSubmitInputFieldUiState(
    val repositoryInfo: String = "",
    val accountInfo: String = "",
    val accountOwner: String = ""
)