package com.gurumlab.wish.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun ChatRoomScreen(
    chatRoom: ChatRoom,
    otherUserName: String,
    otherUserImageUrl: String,
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
            otherUserName = otherUserName,
            otherUserImageUrl = otherUserImageUrl,
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
    viewModel: ChatRoomViewModel,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = remember { configuration.screenWidthDp.dp }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        when (uiState) {
            ChatRoomUiState.Loading -> {
                ChatRoomLoadingScreen(modifier = Modifier.weight(1f))
            }

            ChatRoomUiState.Fail -> {
                ChatRoomFailScreen(modifier = Modifier.weight(1f))
            }

            is ChatRoomUiState.Success -> {
                val messages = (uiState as ChatRoomUiState.Success).messages
                ChatList(
                    chatList = messages,
                    currentUserUid = viewModel.getUid(),
                    otherUserName = otherUserName,
                    otherUserImageUrl = otherUserImageUrl,
                    context = context,
                    screenWidth = screenWidth,
                    onRepository = onRepository,
                    onDonation = onDonation,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ChatInput(
            text = viewModel.messageUiState.message,
            isEnabled = viewModel.messageUiState.isChatEnabled,
            onTextChange = { viewModel.updateMessage(it) },
            onSendClick = { viewModel.sendMessage() }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }

    LaunchedEffect(uiState) {
        viewModel.updateIsChatEnabled(
            when (uiState) {
                is ChatRoomUiState.Success -> true
                else -> false
            }
        )
    }
}