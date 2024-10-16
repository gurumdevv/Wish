package com.gurumlab.wish.ui.message

import android.util.Log
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val repository: ChatRoomRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ChatRoomUiState> =
        MutableStateFlow(ChatRoomUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private lateinit var chatRoomDetailUiState: ChatRoomDetailUiState

    var messageUiState by mutableStateOf(MessageUiState())
        private set

    private val databaseRef = repository.getFirebaseDatabaseRef()
    private lateinit var messagesRef: DatabaseReference
    private lateinit var messagesQuery: Query

    private val fireStore = repository.getFireStore()
    private lateinit var myFireStoreRef: DocumentReference
    private lateinit var othersFireStoreRef: DocumentReference

    fun getChatRoom(roomId: String, othersUid: String, chatRoom: ChatRoom) {
        val uid = repository.getCurrentUser()?.uid ?: ""

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
        resetMyNotReadMessageCount()
    }

    private fun getMessage() {
        messagesQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                _uiState.value = ChatRoomUiState.Success(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ChatRoomViewModel", "Listen failed ${error.toException()}")
                _uiState.value = ChatRoomUiState.Fail
            }
        })
    }

    fun sendMessage() {
        val message = messageUiState.message
        if (message.isBlank()) return

        viewModelScope.launch {
            updateMessageUiState(isChatEnabled = false)

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
                updateMessageUiState(message = "", isChatEnabled = true)
            } else {
                Log.d("ChatRoomViewModel", "Failed to add message")
                updateMessageUiState(isChatEnabled = true)
            }
        }
    }

    private fun resetMyNotReadMessageCount() {
        myFireStoreRef.update(mapOf(Constants.NOT_READ_MESSAGE_COUNT to 0))
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

    fun updateMessageUiState(
        message: String? = null,
        isChatEnabled: Boolean? = null
    ) {
        messageUiState = messageUiState.copy(
            message = message ?: messageUiState.message,
            isChatEnabled = isChatEnabled ?: messageUiState.isChatEnabled
        )
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
    val isChatEnabled: Boolean = false
)