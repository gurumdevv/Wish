package com.gurumlab.wish.ui.message

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor() : ViewModel() {
    private var roomId: String = ""
    private val database = Firebase.database
    private lateinit var messagesRef: DatabaseReference
    private lateinit var messagesQuery: Query
    private val uid: String = Firebase.auth.currentUser?.uid ?: ""

    private val _message = mutableStateOf("")
    val message: State<String> = _message
    private var _messages = MutableStateFlow(emptyList<Chat>())
    val messages = _messages.asStateFlow()
    private var _isChatEnabled = mutableStateOf(true)
    val isChatEnabled: State<Boolean> = _isChatEnabled

    fun setRoomId(roomId: String) {
        this.roomId = roomId
        messagesRef =
            database.getReference().child(Constants.MESSAGES).child(roomId)
        messagesQuery =
            database.getReference().child(Constants.MESSAGES).child(roomId).orderByChild("sentAt")
        getMessage()
    }

    private fun getMessage() {
        messagesQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                _messages.value = chatList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ChatViewModel", "Listen failed.", error.toException())
            }
        })
    }

    fun addMessage() {
        val message = _message.value
        if (message.isNotBlank()) {
            _isChatEnabled.value = false
            val newChat = Chat(
                uid = uid,
                message = message,
                sentAt = System.currentTimeMillis()
            )

            messagesRef.push().setValue(newChat)
                .addOnSuccessListener {
                    _message.value = ""
                    _isChatEnabled.value = true
                }
                .addOnFailureListener {
                    Log.w("ChatViewModel", "Failed to add message")
                    _isChatEnabled.value = true
                }
        }
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }
}