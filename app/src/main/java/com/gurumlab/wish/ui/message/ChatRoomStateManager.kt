package com.gurumlab.wish.ui.message

class ChatRoomStateManager {

    private var currentChatRoomId: String? = null

    fun getCurrentChatRoomId(): String? = currentChatRoomId

    fun updateCurrentChatRoomId(roomId: String) {
        currentChatRoomId = roomId
    }

    fun clearCurrentChatRoomId() {
        currentChatRoomId = null
    }
}