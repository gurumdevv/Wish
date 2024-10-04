package com.gurumlab.wish.ui.message

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.ChatRoom

@Composable
fun ChatsRoute(onChatRoom: (ChatRoom, otherUserName: String, otherUserImageUrl: String) -> Unit) {
    val viewModel: ChatsViewModel = hiltViewModel()
    ChatsScreen(viewModel, onChatRoom)
}

@Composable
fun ChatRoomRoute(
    chatRoom: ChatRoom,
    otherUserName: String,
    otherUserImageUrl: String
) {
    val viewModel: ChatRoomViewModel = hiltViewModel()
    ChatRoomScreen(
        viewModel = viewModel,
        chatRoom = chatRoom,
        otherUserName = otherUserName,
        otherUserImageUrl = otherUserImageUrl
    )
}