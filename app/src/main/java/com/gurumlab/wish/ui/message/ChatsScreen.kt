package com.gurumlab.wish.ui.message

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.DateTimeConverter
import com.gurumlab.wish.ui.util.toDp

@Composable
fun ChatsScreen(
    viewModel: ChatsViewModel,
    onChatRoom: (ChatRoom, String, String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.getChatRooms()
    }

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
    onChatRoom: (ChatRoom, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val chatRooms by viewModel.chatRooms.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        ChatRoomTitle()
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(chatRooms.count()) { index ->
                val othersUid = chatRooms[index].othersUid
                val otherUserInfo = viewModel.userInfos[othersUid]
                val othersName = otherUserInfo?.name ?: stringResource(R.string.name)
                val othersProfileImageUrl = otherUserInfo?.profileImageUrl ?: ""

                ChatsItem(
                    chatRoom = chatRooms[index],
                    othersName = othersName,
                    othersProfileImageUrl = othersProfileImageUrl,
                    context = context,
                    onChatRoom = onChatRoom
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ChatRoomTitle(
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.share_idea_each_others),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ChatsItem(
    chatRoom: ChatRoom,
    othersName: String,
    context: Context,
    othersProfileImageUrl: String,
    onChatRoom: (ChatRoom, String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onChatRoom(chatRoom, othersName, othersProfileImageUrl)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = othersProfileImageUrl.ifBlank {
                ImageRequest.Builder(context).data(R.drawable.ic_profile).build()
            },
            contentDescription = stringResource(id = R.string.profile_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = othersName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chatRoom.lastMessage,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (chatRoom.notReadMessageCount > 0) {
            Box(
                modifier = Modifier
                    .width(14.sp.toDp() + 8.dp)
                    .clip(CircleShape)
                    .background(Color.Red),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = chatRoom.notReadMessageCount.toString(),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        } else {
            val dateTime = DateTimeConverter.getDateTime(chatRoom.lastMessageSentAt!!, context)
            Text(text = dateTime, color = Color.White, fontSize = 14.sp)
        }
    }
}