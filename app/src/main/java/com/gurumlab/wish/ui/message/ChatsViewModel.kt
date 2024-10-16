package com.gurumlab.wish.ui.message

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.data.repository.ChatsRepository
import com.gurumlab.wish.ui.util.Constants
import com.gurumlab.wish.ui.util.NetWorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val repository: ChatsRepository,
    private val netWorkManager: NetWorkManager
) : ViewModel() {

    private val database = repository.getFirebaseDatabase()

    private val _uiState = MutableStateFlow<ChatsUiState>(ChatsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var userInfos = mutableStateMapOf<String, UserInfo>()
        private set

    fun getChatRooms() {
        if (!netWorkManager.isOnline()) {
            _uiState.value = ChatsUiState.Fail
            return
        }

        val uid = repository.getCurrentUser()?.uid ?: ""
        val fireStore = repository.getFireStore()
        val chatsRef = fireStore.collection(uid)
            .orderBy(Constants.LAST_SENT_AT, Query.Direction.DESCENDING)

        chatsRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d("ChatsViewModel", "Listen failed ${error.message}")
                _uiState.value = ChatsUiState.Fail
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val chatRoomList =
                    snapshot.documents.mapNotNull { it.toObject(ChatRoom::class.java) }

                viewModelScope.launch {
                    chatRoomList.map { chatRoom ->
                        async { getUserInfo(chatRoom.othersUid) }
                    }.awaitAll()

                    _uiState.value = ChatsUiState.Success(chatRoomList)
                }
            } else {
                _uiState.value = ChatsUiState.Success(emptyList())
            }
        }
    }

    private suspend fun getUserInfo(uid: String) {
        try {
            val dataSnapshot =
                database.getReference().child(Constants.AUTH).child(uid).get().await()
            val userInfo = dataSnapshot.getValue(UserInfo::class.java)

            if (userInfo != null) {
                userInfos[uid] = userInfo
            } else {
                Log.d("ChatsViewModel", "No user info found for UID: $uid")
            }
        } catch (e: Exception) {
            Log.d("ChatsViewModel", "Error getting data for UID: $uid - ${e.message}")
        }
    }
}

sealed class ChatsUiState {
    data object Loading : ChatsUiState()
    data object Fail : ChatsUiState()
    data class Success(val chatRooms: List<ChatRoom>) : ChatsUiState()
}