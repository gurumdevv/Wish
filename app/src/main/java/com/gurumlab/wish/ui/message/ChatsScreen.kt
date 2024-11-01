package com.gurumlab.wish.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel,
    onChatRoom: (ChatRoom, String, String, String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        ChatsContent(
            viewModel = viewModel,
            onChatRoom = onChatRoom,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun ChatsContent(
    viewModel: ChatsViewModel,
    onChatRoom: (ChatRoom, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        ChatRoomTitle()
        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is ChatsUiState.Loading -> {
                ChatsLoadingScreen()
            }

            is ChatsUiState.Fail -> {
                ChatFailScreen()
            }

            is ChatsUiState.Success -> {
                val chatRooms = (uiState as ChatsUiState.Success).chatRooms

                LazyColumn {
                    items(chatRooms.count()) { index ->
                        val othersUid = chatRooms[index].othersUid
                        val otherUserInfo = viewModel.userInfos[othersUid]
                        val othersName = otherUserInfo?.name ?: stringResource(R.string.name)
                        val othersProfileImageUrl = otherUserInfo?.profileImageUrl ?: ""
                        val othersFcmToken = otherUserInfo?.fcmToken ?: ""

                        ChatsItem(
                            chatRoom = chatRooms[index],
                            othersName = othersName,
                            othersProfileImageUrl = othersProfileImageUrl,
                            othersFcmToken = othersFcmToken,
                            context = context,
                            onChatRoom = onChatRoom
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}