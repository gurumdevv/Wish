package com.gurumlab.wish.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.ErrorSnackBar
import com.gurumlab.wish.ui.util.showSnackbar

@Composable
fun ChatRoomScreen(
    chatRoom: ChatRoom,
    othersUserName: String,
    othersUserImageUrl: String,
    othersFcmToken: String,
    viewModel: ChatRoomViewModel,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.getChatRoom(
            roomId = chatRoom.id,
            othersUid = chatRoom.othersUid,
            chatRoom = chatRoom
        )
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        ChatRoomContent(
            otherUserName = othersUserName,
            otherUserImageUrl = othersUserImageUrl,
            othersFcmToken = othersFcmToken,
            viewModel = viewModel,
            onRepository = onRepository,
            onDonation = onDonation,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun ChatRoomContent(
    otherUserName: String,
    otherUserImageUrl: String,
    othersFcmToken: String, //TODO: 메세지 푸시 보내기 기능 구현에 사용
    viewModel: ChatRoomViewModel,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = remember { configuration.screenWidthDp.dp }
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    Box(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                ChatRoomUiState.Loading -> {
                    ChatRoomLoadingScreen(modifier = Modifier.weight(1f))
                }

                ChatRoomUiState.Fail -> {
                    ChatFailScreen(modifier = Modifier.weight(1f))
                }

                is ChatRoomUiState.Success -> {
                    val messages = (uiState as ChatRoomUiState.Success).messages
                    ChatList(
                        state = listState,
                        chatList = messages,
                        currentUserUid = viewModel.getUid(),
                        otherUserName = otherUserName,
                        otherUserImageUrl = otherUserImageUrl,
                        screenWidth = screenWidth,
                        isReverse = true,
                        onRepository = onRepository,
                        onDonation = onDonation,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            ChatInput(
                text = viewModel.messageUiState.message,
                isEnabled = viewModel.messageUiState.isChatEnabled,
                onTextChange = { viewModel.updateMessage(it) },
                onSendClick = { viewModel.sendMessage() },
                modifier = Modifier
                    .consumeWindowInsets(WindowInsets.navigationBars)
                    .imePadding()
            )
        }

        ErrorSnackBar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        )
    }

    LaunchedEffect(uiState) {
        viewModel.updateIsChatEnabled(
            when (uiState) {
                is ChatRoomUiState.Success -> true
                else -> false
            }
        )
    }

    LaunchedEffect(viewModel.messageUiState.isChatError) {
        if (viewModel.messageUiState.isChatError) {
            viewModel.handleSendingMessageError(R.string.sending_message_error)
        }
    }

    LaunchedEffect(viewModel.snackbarMessageRes.value) {
        showSnackbar(
            snackbarMessageRes = viewModel.snackbarMessageRes.value,
            context = context,
            snackbarHostState = snackbarHostState,
        )
    }
}