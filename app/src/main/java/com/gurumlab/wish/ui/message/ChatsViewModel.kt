package com.gurumlab.wish.ui.message

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.data.repository.ChatsRepository
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val repository: ChatsRepository
) : ViewModel() {

    private val _chatRooms = MutableStateFlow(emptyList<ChatRoom>())
    val chatRooms = _chatRooms.asStateFlow()

    var userInfos = mutableStateMapOf<String, UserInfo>()
        private set

    fun getChatRooms() {
        val uid = repository.getCurrentUser()?.uid ?: ""
        val fireStore = repository.getFireStore()
        val chatsRef = fireStore.collection(uid)
            .orderBy(Constants.LAST_SENT_AT, Query.Direction.DESCENDING)

        chatsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d("ChatsViewModel", "Listen failed ${error.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val chatRoomList =
                    snapshot.documents.mapNotNull { it.toObject(ChatRoom::class.java) }
                _chatRooms.value = chatRoomList
                chatRoomList.forEach { chatRoom ->
                    getUserInfo(chatRoom.othersUid)
                }
            } else {
                Log.d("ChatsViewModel", "Current data: null")
            }
        }
    }

    private fun getUserInfo(uid: String) {
        val database = repository.getFirebaseDatabase()
        database.getReference().child(Constants.AUTH).child(uid).get()
            .addOnSuccessListener {
                val userInfo = it.getValue(UserInfo::class.java)
                if (userInfo != null) {
                    userInfos[uid] = userInfo
                }
            }
            .addOnFailureListener {
                Log.d("ChatsViewModel", "Error getting data ${it.message}")
            }
    }
}