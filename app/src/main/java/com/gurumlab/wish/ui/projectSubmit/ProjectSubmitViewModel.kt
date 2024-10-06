package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.repository.ProjectSubmitRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.single
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
        emptySnackbarMessageRes: Int,
        failSnackbarMessageRes: Int,
        projectCompletedString: String
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
                val completedWishId = repository.submitWish(
                    completedWish = completedWish,
                    onErrorOrException = {
                        _snackbarMessage.value = failSnackbarMessageRes
                        _isLoading.value = false

                    },
                ).single()
                sendMessage(
                    completedWishId = completedWishId.values.first(),
                    minimizedWish = minimizedWish,
                    projectCompletedString = projectCompletedString
                ) { isSuccess ->
                    _isSubmitSuccess.value = isSuccess
                    if (isSuccess) {
                        updateWishStatus(wishId)
                    }
                }
            }
        } else {
            _snackbarMessage.value = emptySnackbarMessageRes
        }
    }

    private fun sendMessage(
        completedWishId: String,
        minimizedWish: MinimizedWish,
        projectCompletedString: String,
        callback: (Boolean) -> Unit
    ) {
        val uid = Firebase.auth.currentUser?.uid ?: ""
        val othersUid = minimizedWish.posterId
        val roomId = "${uid}+${othersUid}"

        val fireStore = Firebase.firestore
        val myFireStoreRef = fireStore.collection(uid).document(roomId)
        val othersFireStoreRef = fireStore.collection(othersUid).document(roomId)
        val database = Firebase.database
        val messagesRef = database.getReference().child(Constants.MESSAGES).child(roomId)
        val batch = Firebase.firestore.batch()

        val newChat = Chat(
            uid = uid,
            message = completedWishId,
            sentAt = System.currentTimeMillis(),
            isSubmit = true
        )

        val currentTimeStamp = FieldValue.serverTimestamp()
        val chatRoomData = mutableMapOf(
            Constants.LAST_MESSAGE to projectCompletedString,
            Constants.LAST_SENT_AT to currentTimeStamp
        )

        messagesRef.push().setValue(newChat)
            .addOnSuccessListener {
                batch.update(myFireStoreRef, chatRoomData)
                batch.update(othersFireStoreRef, chatRoomData)
                batch.update(
                    othersFireStoreRef,
                    Constants.NOT_READ_MESSAGE_COUNT,
                    FieldValue.increment(1)
                )
                batch.commit().addOnSuccessListener {
                    callback(true)
                }.addOnFailureListener {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    private fun updateWishStatus(wishId: String) {
        viewModelScope.launch {
            _isUpdateSuccess.value = repository.updateWishStatus(wishId, 2)
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