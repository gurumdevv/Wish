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

    private var tempChatRoomId = ""

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

    fun setTempChatRoomIdToCurrentChatRoomId() {
        tempChatRoomId = getCurrentChatRoomId()
    }

    fun setCurrentChatRoomIdToTempChatRoomId() {
        if (chatRoomStateManager.getIsChatRoomViewModelExist()) {
            chatRoomStateManager.updateCurrentChatRoomId(tempChatRoomId)
        }
    }

    fun getCurrentChatRoomId(): String {
        return chatRoomStateManager.getCurrentChatRoomId() ?: "" //채팅방을 나가면 "" 반환
    }

    fun clearCurrentChatRoomId() {
        chatRoomStateManager.clearCurrentChatRoomId()
    }
}

/**
구현 시나리오
1. 채팅방에서 홈화면으로 나갔다가 다시 들어왔을 때
a. currentChatRoomId를 가져와서 tempRoomId로 설정 (onStop)
b. currentChatRoomId를 null로 초기화 (onStop)
c. currentChatRoomId를 tempRoomId로 다시 설정 (onStart)

2. 채팅방에서 홈화면으로 나갔다가 다시 들어오고 채팅방에서 나간 후 홈화면으로 갔다가 다시 앱으로 들어왔을 때
a. currentChatRoomId를 가져와서 tempRoomId로 설정 (onStop)
b. currentChatRoomId를 null로 초기화(onStop)
c. currentChatRoomId를 tempRoomId로로 다시 설정 (onStart)
d. 채팅방에서 나가면서 ChatRoomViewModel에서 currentChatRoomId를 null로 초기화 (onCleared)
e. 홈화면으로 나가면서 setTempChatRoomIdToCurrentChatRoomId이 호출됨. 이 함수 내에서  getCurrentChatRoomId 호출되고, 그 반환값이 null이기 때문에 빈문자열("")이 반환됨
f. getCurrentChatRoomId에서 빈문자열("")이 호출됬기 때문에 tempRoomId는 빈문자열("")로 초기화됨 (onStop)
g. currentChatRoomId를 null로 초기화 (onStop)
h. 홈화면에서 앱으로 다시 돌아왔을 때 setCurrentChatRoomIdToTempChatRoomId이 호출되지만 getIsChatRoomViewModelExist의 값이 false이기 때문에 tempRoomId는 초기화되지 않음 (onStart)
 **/