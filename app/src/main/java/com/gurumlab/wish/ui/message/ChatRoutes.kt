package com.gurumlab.wish.ui.message

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.util.CustomTopAppBar
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@Composable
fun ChatsRoute(
    bottomNavigationBar: @Composable () -> Unit,
    onChatRoom: (chatRoom: ChatRoom, othersUserName: String, othersUserImageUrl: String, othersFcmToken: String) -> Unit
) {
    val viewModel = hiltViewModel<ChatsViewModel>()
    val topBar: @Composable () -> Unit = { CustomTopAppBar(stringResource(id = R.string.chats)) }
    ChatsScreen(
        viewModel = viewModel,
        onChatRoom = onChatRoom,
        topBar = topBar,
        bottomBar = bottomNavigationBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomRoute(
    chatRoom: ChatRoom,
    othersUserName: String,
    othersUserImageUrl: String,
    othersFcmToken: String,
    onNavUp: () -> Unit,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit
) {
    val topBar: @Composable () -> Unit =
        { CustomTopAppBarWithButton(title = othersUserName, onNavIconPressed = onNavUp) }
    val viewModel: ChatRoomViewModel = hiltViewModel()
    ChatRoomScreen(
        chatRoom = chatRoom,
        othersUserName = othersUserName,
        othersUserImageUrl = othersUserImageUrl,
        othersFcmToken = othersFcmToken,
        viewModel = viewModel,
        onRepository = onRepository,
        onDonation = onDonation,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationRoute(
    completedWishId: String,
    viewModel: SubmissionViewModel,
    onNavUp: () -> Unit,
    onClosed: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    DonationScreen(
        viewModel = viewModel,
        completedWishId = completedWishId,
        onClosed = onClosed,
        topBar = topBar
    )
}

@Composable
fun RepositoryRedirectRoute(
    completedWishId: String,
    viewModel: SubmissionViewModel,
    onBack: () -> Unit
) {
    RepositoryRedirectScreen(
        completedWishId = completedWishId,
        viewModel = viewModel,
        onBack = onBack
    )
}