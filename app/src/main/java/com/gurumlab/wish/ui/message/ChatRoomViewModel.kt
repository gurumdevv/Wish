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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor() : ViewModel() {
    private var roomId = ""
    private var othersUid = ""
    private lateinit var chatRoom: ChatRoom
    private val database = Firebase.database
    private lateinit var messagesRef: DatabaseReference
    private lateinit var messagesQuery: Query
    private val uid = Firebase.auth.currentUser?.uid ?: ""
    private val fireStore = Firebase.firestore
    private lateinit var myFireStoreRef: DocumentReference
    private lateinit var othersFireStoreRef: DocumentReference

    private val _message = mutableStateOf("")
    val message: State<String> = _message
    private var _messages = MutableStateFlow(emptyList<Chat>())
    val messages = _messages.asStateFlow()
    private var _isChatEnabled = mutableStateOf(true)
    val isChatEnabled: State<Boolean> = _isChatEnabled

    fun initializeChatRoom(chatRoom: ChatRoom, roomId: String, othersUid: String) {
        this.chatRoom = chatRoom
        this.roomId = roomId
        this.othersUid = othersUid
        messagesRef =
            database.getReference().child(Constants.MESSAGES).child(roomId)
        messagesQuery =
            database.getReference().child(Constants.MESSAGES).child(roomId)
                .orderByChild(Constants.SENT_AT)
        myFireStoreRef = fireStore.collection(uid).document(roomId)
        othersFireStoreRef = fireStore.collection(othersUid).document(roomId)

        getMessage()
        resetMyNotReadMessageCount()
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
                sentAt = System.currentTimeMillis(),
                submission = false
            )

            messagesRef.push().setValue(newChat)
                .addOnSuccessListener {
                    updateFireStore(message)
                    _message.value = ""
                    _isChatEnabled.value = true
                }
                .addOnFailureListener {
                    Log.w("ChatViewModel", "Failed to add message")
                    _isChatEnabled.value = true
                }
        }
    }

    private fun updateFireStore(currentMessage: String) {
        val currentTimeStamp = FieldValue.serverTimestamp()
        val batch = Firebase.firestore.batch()

        val myChatRoomData = mutableMapOf(
            Constants.LAST_MESSAGE to currentMessage,
            Constants.LAST_SENT_AT to currentTimeStamp
        ).apply {
            if (chatRoom.lastMessageSentAt == null) {
                put(Constants.ID, roomId)
                put(Constants.OTHERS_UID, othersUid)
                put(Constants.NOT_READ_MESSAGE_COUNT, 0)
            }
        }

        val othersChatRoomData = mutableMapOf(
            Constants.LAST_MESSAGE to currentMessage,
            Constants.LAST_SENT_AT to currentTimeStamp
        ).apply {
            if (chatRoom.lastMessageSentAt == null) {
                put(Constants.ID, roomId)
                put(Constants.OTHERS_UID, uid)
                put(Constants.NOT_READ_MESSAGE_COUNT, 1)
            }
        }

        if (chatRoom.lastMessageSentAt == null) {
            batch.set(myFireStoreRef, myChatRoomData)
            batch.set(othersFireStoreRef, othersChatRoomData)
        } else {
            batch.update(myFireStoreRef, myChatRoomData)
            batch.update(othersFireStoreRef, othersChatRoomData)
            batch.update(
                othersFireStoreRef,
                Constants.NOT_READ_MESSAGE_COUNT,
                FieldValue.increment(1)
            )
        }

        batch.commit().addOnSuccessListener {
            Log.d("updateFireStore", "Chat room updated successfully")
        }.addOnFailureListener { e ->
            Log.e("updateFireStore", "Error updating chat room", e)
        }
    }


    private fun resetMyNotReadMessageCount() {
        myFireStoreRef.update(mapOf(Constants.NOT_READ_MESSAGE_COUNT to 0))
    }

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }
}