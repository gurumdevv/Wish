package com.gurumlab.wish.ui.message

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
    onChatRoom: (ChatRoom, otherUserName: String, otherUserImageUrl: String) -> Unit
) {
    val viewModel: ChatsViewModel = hiltViewModel()
    val topBar: @Composable () -> Unit = {
        CustomTopAppBar(
            stringResource(id = R.string.chats)
        )
    }
    ChatsScreen(
        topBar = topBar,
        bottomBar = bottomNavigationBar,
        viewModel = viewModel,
        onChatRoom = onChatRoom
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomRoute(
    chatRoom: ChatRoom,
    otherUserName: String,
    otherUserImageUrl: String,
    onNavUp: () -> Unit,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    val viewModel: ChatRoomViewModel = hiltViewModel()
    ChatRoomScreen(
        topBar = topBar,
        viewModel = viewModel,
        chatRoom = chatRoom,
        otherUserName = otherUserName,
        otherUserImageUrl = otherUserImageUrl,
        onRepository = onRepository,
        onDonation = onDonation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationRoute(
    viewModel: SubmissionViewModel,
    completedWishId: String,
    onNavUp: () -> Unit,
    onClosed: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    DonationScreen(
        topBar = topBar,
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