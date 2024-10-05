package com.gurumlab.wish.ui.message

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor() : ViewModel() {

    private val uid: String = Firebase.auth.currentUser?.uid ?: ""
    private val db = Firebase.firestore
    private val chatsRef = db.collection(uid)
        .orderBy(Constants.LAST_SENT_AT, Query.Direction.DESCENDING)
    private val database = Firebase.database

    private val _chatRooms = MutableStateFlow(emptyList<ChatRoom>())
    val chatRooms = _chatRooms.asStateFlow()

    private val _userInfos = mutableStateMapOf<String, UserInfo>()
    val userInfos: Map<String, UserInfo> = _userInfos

    init {
        getChatRooms()
    }

    private fun getChatRooms() {
        chatsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("ChatsViewModel", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val chatRoomList =
                    snapshot.documents.mapNotNull { it.toObject(ChatRoom::class.java) }
                _chatRooms.value = chatRoomList
                chatRoomList.forEach { chatRoom ->
                    setUserInfo(chatRoom.othersUid)
                }
            } else {
                Log.d("ChatsViewModel", "Current data: null")
            }
        }
    }

    private fun setUserInfo(uid: String) {
        database.getReference().child(Constants.AUTH).child(uid).get()
            .addOnSuccessListener {
                val userInfo = it.getValue(UserInfo::class.java)
                if (userInfo != null) {
                    _userInfos[uid] = userInfo
                }
            }
            .addOnFailureListener {
                Log.e("ChatsViewModel", "Error getting data", it)
            }
    }
}
