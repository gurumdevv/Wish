package com.gurumlab.wish.ui.message

class ChatRoomStateManager {

    private var currentChatRoomId: String? = null
    private var isChatRoomViewModelExist: Boolean = false

    fun getCurrentChatRoomId(): String? = currentChatRoomId

    fun updateCurrentChatRoomId(roomId: String) {
        currentChatRoomId = roomId
    }

    fun clearCurrentChatRoomId() {
        currentChatRoomId = null
    }

    fun getIsChatRoomViewModelExist(): Boolean = isChatRoomViewModelExist

    fun setIsChatRoomViewModelExist(isExist: Boolean) {
        isChatRoomViewModelExist = isExist
    }
}