package com.gurumlab.wish.ui.message

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentReference
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.repository.ChatRoomRepository
import com.gurumlab.wish.ui.util.Constants
import com.gurumlab.wish.ui.util.NetWorkManager
import com.gurumlab.wish.util.addMessage
import com.gurumlab.wish.util.updateChatRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val repository: ChatRoomRepository,
    private val netWorkManager: NetWorkManager,
    private val chatRoomStateManager: ChatRoomStateManager
) : ViewModel() {

    private val _uiState: MutableStateFlow<ChatRoomUiState> =
        MutableStateFlow(ChatRoomUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var chatRoomDetailUiState: ChatRoomDetailUiState

    var messageUiState by mutableStateOf(MessageUiState())
        private set

    var snackbarMessageRes: MutableState<Int?> = mutableStateOf(null)
        private set

    private val databaseRef = repository.getFirebaseDatabaseRef()
    private lateinit var messagesRef: DatabaseReference
    private lateinit var messagesQuery: Query

    private val fireStore = repository.getFireStore()
    private lateinit var myFireStoreRef: DocumentReference
    private lateinit var othersFireStoreRef: DocumentReference

    fun getChatRoom(roomId: String, othersUid: String, chatRoom: ChatRoom) {
        val uid = repository.getCurrentUser()?.uid ?: ""
        val isOnline = netWorkManager.isOnline()

        if (!isOnline) {
            _uiState.value = ChatRoomUiState.Fail
            return
        }

        chatRoomStateManager.updateCurrentChatRoomId(roomId)
        chatRoomStateManager.setIsChatRoomViewModelExist(true)

        chatRoomDetailUiState = ChatRoomDetailUiState(
            chatRoom = chatRoom,
            roomId = roomId,
            uid = uid,
            othersUid = othersUid
        )

        messagesRef = databaseRef.child(Constants.MESSAGES).child(roomId)
        messagesQuery = messagesRef.orderByChild(Constants.SENT_AT)

        myFireStoreRef = fireStore.collection(uid).document(roomId)
        othersFireStoreRef = fireStore.collection(othersUid).document(roomId)

        getMessage()
        resetNotReadMessageCount()
    }

    private fun getMessage() {
        messagesQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                viewModelScope.launch(Dispatchers.Default) {
                    val reversedChatList = chatList.reversed()
                    _uiState.value = ChatRoomUiState.Success(reversedChatList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatRoomViewModel", "Listen failed ${error.toException()}")
                _uiState.value = ChatRoomUiState.Fail
            }
        })
    }

    fun sendMessage(othersFcmToken: String, defaultTitle: String) {
        val message = messageUiState.message
        if (message.isBlank()) return

        if (!netWorkManager.isOnline()) {
            updateMessageUiState(isChatError = true)
            return
        }

        viewModelScope.launch {
            updateMessageUiState(isChatEnabled = false)
            updateMessageUiState(message = "")

            val result = addMessage(
                uid = chatRoomDetailUiState.uid,
                message = message,
                isSubmission = false,
                messagesRef = messagesRef
            )

            if (result) {
                updateChatRoom(
                    message = message,
                    chatRoom = chatRoomDetailUiState.chatRoom!!,
                    roomId = chatRoomDetailUiState.roomId,
                    myUid = chatRoomDetailUiState.uid,
                    othersUid = chatRoomDetailUiState.othersUid,
                    myFireStoreRef = myFireStoreRef,
                    othersFireStoreRef = othersFireStoreRef
                )

                sendPushMessage(
                    chatRoomId = chatRoomDetailUiState.roomId,
                    othersFcmToken = othersFcmToken,
                    message = message,
                    defaultTitle = defaultTitle
                )

                updateMessageUiState(isChatEnabled = true)
            } else {
                Log.d("ChatRoomViewModel", "Failed to add message")
                updateMessageUiState(message = message, isChatEnabled = true, isChatError = true)
            }
        }
    }

    private fun resetNotReadMessageCount() {
        myFireStoreRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                myFireStoreRef.update(mapOf(Constants.NOT_READ_MESSAGE_COUNT to 0))
            }
        }
    }

    fun getUid(): String {
        return repository.getCurrentUser()?.uid ?: ""
    }

    fun updateMessage(newMessage: String) {
        updateMessageUiState(message = newMessage)
    }

    fun updateIsChatEnabled(isChatEnabled: Boolean) {
        updateMessageUiState(isChatEnabled = isChatEnabled)
    }

    private fun updateMessageUiState(
        message: String? = null,
        isChatEnabled: Boolean? = null,
        isChatError: Boolean? = null
    ) {
        messageUiState = messageUiState.copy(
            message = message ?: messageUiState.message,
            isChatEnabled = isChatEnabled ?: messageUiState.isChatEnabled,
            isChatError = isChatError ?: messageUiState.isChatError
        )
    }

    fun handleSendingMessageError(messageRes: Int) {
        showSnackbarMessage(messageRes)
        updateMessageUiState(isChatError = false)
    }

    private fun showSnackbarMessage(messageRes: Int) {
        viewModelScope.launch {
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

    override fun onCleared() {
        super.onCleared()
        chatRoomStateManager.clearCurrentChatRoomId()
        chatRoomStateManager.setIsChatRoomViewModelExist(false)
    }
}

sealed class ChatRoomUiState {
    data object Loading : ChatRoomUiState()
    data object Fail : ChatRoomUiState()
    data class Success(val messages: List<Chat>) : ChatRoomUiState()
}

data class ChatRoomDetailUiState(
    val chatRoom: ChatRoom? = null,
    val roomId: String = "",
    val uid: String = "",
    val othersUid: String = ""
)

data class MessageUiState(
    val message: String = "",
    val isChatEnabled: Boolean = false,
    val isChatError: Boolean = false
)