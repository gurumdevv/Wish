package com.gurumlab.wish.ui.message

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.ChatRoom

@Composable
fun ChatsRoute(onChatRoom: (ChatRoom, otherUserName: String, otherUserImageUrl: String) -> Unit) {
    val viewModel: ChatsViewModel = hiltViewModel()
    ChatsScreen(viewModel = viewModel, onChatRoom = onChatRoom)
}

@Composable
fun ChatRoomRoute(
    chatRoom: ChatRoom,
    otherUserName: String,
    otherUserImageUrl: String,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit
) {
    val viewModel: ChatRoomViewModel = hiltViewModel()
    ChatRoomScreen(
        viewModel = viewModel,
        chatRoom = chatRoom,
        otherUserName = otherUserName,
        otherUserImageUrl = otherUserImageUrl,
        onRepository = onRepository,
        onDonation = onDonation
    )
}

@Composable
fun DonationRoute(
    viewModel: SubmissionViewModel,
    completedWishId: String,
    onClosed: () -> Unit
) {
    DonationScreen(
        viewModel = viewModel,
        completedWishId = completedWishId,
        onClosed = onClosed
    )
}

@Composable
fun RepositoryRedirectRoute(
    viewModel: SubmissionViewModel,
    completedWishId: String,
    onBack: () -> Unit
) {
    RepositoryRedirectScreen(
        viewModel = viewModel,
        completedWishId = completedWishId,
        onBack = onBack
    )
}