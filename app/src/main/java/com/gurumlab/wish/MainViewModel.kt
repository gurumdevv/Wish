package com.gurumlab.wish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.ChatRoomInfo
import com.gurumlab.wish.data.model.UserInfo
import com.gurumlab.wish.data.repository.MainRepository
import com.gurumlab.wish.ui.message.ChatRoomStateManager
import com.gurumlab.wish.ui.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository,
    private val chatRoomStateManager: ChatRoomStateManager
) : ViewModel() {

    private val _uid: MutableStateFlow<String?> = MutableStateFlow(null)
    val uid = _uid.asStateFlow()

    private val _chatRoomInfo: MutableStateFlow<ChatRoomInfo?> = MutableStateFlow(null)
    val chatRoomInfo = _chatRoomInfo.asStateFlow()

    init {
        _uid.value = getUid()
    }

    private fun getUid(): String {
        return repository.getCurrentUser()?.uid ?: ""
    }

    fun loadChatRoomInfo(chatRoomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val chatRoom = getChatRoom(chatRoomId)
            val othersUid = chatRoom.othersUid
            val othersInfo = getOthersInfo(othersUid)
            _chatRoomInfo.value = ChatRoomInfo(chatRoom, othersInfo)
        }
    }

    private suspend fun getChatRoom(chatRoomId: String): ChatRoom {
        val uid = repository.getCurrentUser()?.uid ?: ""
        val result = repository.getFireStore().collection(uid).document(chatRoomId).get().await()
        return result.toObject(ChatRoom::class.java) ?: ChatRoom()
    }

    private suspend fun getOthersInfo(othersUid: String): UserInfo {
        val result =
            repository.getDatabaseReference().child(Constants.AUTH).child(othersUid).get().await()
        return result.getValue(UserInfo::class.java) ?: UserInfo()
    }

    fun getCurrentChatRoomId(): String? {
        return chatRoomStateManager.getCurrentChatRoomId()
    }
}