package com.gurumlab.wish.ui.projectSubmit

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.data.repository.ProjectSubmitRepository
import com.gurumlab.wish.util.addMessage
import com.gurumlab.wish.util.getChatRoom
import com.gurumlab.wish.util.updateChatRoom
import com.gurumlab.wish.ui.util.Constants
import com.gurumlab.wish.ui.util.DateTimeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectSubmitViewModel @Inject constructor(
    private val repository: ProjectSubmitRepository
) : ViewModel() {

    private val _projectUpdateUiState = MutableStateFlow(ProjectUpdateUiState())
    val projectUpdateUiState = _projectUpdateUiState.asStateFlow()

    var projectSubmitInputFieldUiState by mutableStateOf(ProjectSubmitInputFieldUiState())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var snackbarMessageRes: MutableState<Int?> = mutableStateOf(null)
        private set

    fun submitWish(
        wishId: String,
        minimizedWish: MinimizedWish,
        emptySnackbarMessageRes: Int,
        failSnackbarMessageRes: Int,
        projectCompletedDescription: String,
        defaultCompletedMessageTitle: String,
        defaultCompletedMessageBody: String
    ) {
        if (!isInputFieldsValid()) {
            handleError("", emptySnackbarMessageRes)
            return
        }

        val completedWish = fetchCompletedWish(
            minimizedWish = minimizedWish,
            inputFieldUiState = projectSubmitInputFieldUiState,
            currentDate = DateTimeConverter.getCurrentDate()
        )

        viewModelScope.launch {
            isLoading = true

            val idToken = repository.getFirebaseIdToken()
            if (idToken.isBlank()) {
                handleError("idToken is blank", failSnackbarMessageRes)
                return@launch
            }

            val completedWishId = repository.submitWish(
                idToken = idToken,
                completedWish = completedWish
            ).single()

            if (completedWishId.values.isEmpty()) {
                handleError("completedWish uploading failed", failSnackbarMessageRes)
                return@launch
            }

            val isMessageSent = sendMessageWithSubmission(
                completedWishId = completedWishId.values.first(),
                minimizedWish = minimizedWish,
                projectCompletedDescription = projectCompletedDescription,
                defaultCompletedMessageTitle = defaultCompletedMessageTitle,
                defaultCompletedMessageBody = defaultCompletedMessageBody
            )

            if (isMessageSent) {
                _projectUpdateUiState.update { it.copy(isSubmitSuccess = true) }

                updateProjectStatus(
                    idToken = idToken,
                    wishId = wishId,
                    failSnackbarMessageRes = failSnackbarMessageRes
                )
            } else {
                handleError("Message sending failed", failSnackbarMessageRes)
            }
        }
    }

    private fun isInputFieldsValid(): Boolean {
        val state = projectSubmitInputFieldUiState
        return state.repositoryInfo.isNotBlank() &&
                state.accountInfo.isNotBlank() &&
                state.accountOwner.isNotBlank()
    }

    private suspend fun sendMessageWithSubmission(
        completedWishId: String,
        minimizedWish: MinimizedWish,
        projectCompletedDescription: String,
        defaultCompletedMessageTitle: String,
        defaultCompletedMessageBody: String
    ): Boolean {
        val currentUser = repository.getCurrentUser()
        val uid = currentUser?.uid ?: ""
        val othersUid = minimizedWish.posterId
        val roomId = "${uid}+${othersUid}"
        val othersFcmToken = repository.getFCMToken(othersUid)

        val fireStore = repository.getFireStore()
        val myFireStoreRef = fireStore.collection(uid).document(roomId)
        val othersFireStoreRef = fireStore.collection(othersUid).document(roomId)

        val database = repository.getDatabaseRef()
        val messagesRef = database.child(Constants.MESSAGES).child(roomId)

        val isMessageSent = addMessage(
            uid = uid,
            message = completedWishId,
            isSubmission = true,
            messagesRef = messagesRef
        )
        if (!isMessageSent) return false

        sendPushMessage(
            chatRoomId = roomId,
            othersFcmToken = othersFcmToken,
            message = defaultCompletedMessageBody,
            defaultTitle = defaultCompletedMessageTitle
        )

        val chatRoom = getChatRoom(myFireStoreRef) ?: return false

        val isFireStoreUpdated = updateChatRoom(
            message = projectCompletedDescription,
            chatRoom = chatRoom,
            roomId = roomId,
            myUid = uid,
            othersUid = othersUid,
            myFireStoreRef = myFireStoreRef,
            othersFireStoreRef = othersFireStoreRef
        )

        return isFireStoreUpdated
    }

    private suspend fun sendPushMessage(
        chatRoomId: String,
        othersFcmToken: String,
        message: String,
        defaultTitle: String
    ) {
        val myName = repository.getCurrentUser()?.displayName ?: Constants.DEFAULT_USER_NAME
        val title = "$myName $defaultTitle" //예: "피터팬 님으로부터 메시지가 도착했습니다."
        val isSuccess = repository.sendPushMessage(
            chatRoomId = chatRoomId,
            token = othersFcmToken,
            title = title,
            body = message
        )
        if (!isSuccess) {
            Log.d("ChatRoomViewModel", "Failed to send push message")
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
                handleError("Status or Date Update failed", failSnackbarMessageRes)
            }
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


    private fun handleError(logMessage: String, messageRes: Int) {
        viewModelScope.launch {
            Log.d("DetailViewModel", logMessage)
            updateSnackbarMessage(messageRes)
            delay(4000L) //SnackbarDuration.Short
            resetSnackbarMessage()
        }
    }

    private fun updateSnackbarMessage(messageRes: Int) {
        snackbarMessageRes.value = messageRes
    }

    private fun resetSnackbarMessage() {
        snackbarMessageRes.value = null
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

    fun updateProjectSubmitInputFieldChange(
        repositoryInfo: String? = null,
        accountInfo: String? = null,
        accountOwner: String? = null
    ) {
        projectSubmitInputFieldUiState = projectSubmitInputFieldUiState.copy(
            repositoryInfo = repositoryInfo ?: projectSubmitInputFieldUiState.repositoryInfo,
            accountInfo = accountInfo ?: projectSubmitInputFieldUiState.accountInfo,
            accountOwner = accountOwner ?: projectSubmitInputFieldUiState.accountOwner
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