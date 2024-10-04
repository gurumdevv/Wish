package com.gurumlab.wish.ui.message

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultMyMessageItemColor
import com.gurumlab.wish.ui.theme.defaultOtherMessageItemColor
import com.gurumlab.wish.ui.theme.defaultPlaceHolderColor

@Composable
fun ChatRoomScreen(
    viewModel: ChatRoomViewModel,
    currentUserUid: String = "",
    otherUserUid: String = "",
    otherUserName: String = "",
    otherUserImageUrl: String = ""
) {
    ChatRoomContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel,
        currentUserUid = currentUserUid,
        otherUserUid = otherUserUid,
        otherUserName = otherUserName,
        otherUserImageUrl = otherUserImageUrl
    )
}

@Composable
fun ChatRoomContent(
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel,
    currentUserUid: String,
    otherUserUid: String,
    otherUserName: String,
    otherUserImageUrl: String
) {
    val messages = viewModel.messages.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
    ) {
        ChatList(
            modifier = Modifier.weight(1f),
            chatList = messages.value,
            currentUserUid = currentUserUid,
            otherUserUid = otherUserUid,
            otherUserName = otherUserName,
            otherUserImageUrl = otherUserImageUrl
        )
        Spacer(modifier = Modifier.height(16.dp))
        ChatInput(
            text = viewModel.message.value,
            isEnabled = viewModel.isChatEnabled.value,
            onTextChange = { viewModel.updateMessage(it) }) {
            if (viewModel.message.value.isNotBlank()) {
                viewModel.addMessage()
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChatInput(
    text: String,
    isEnabled: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        TextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = onTextChange,
            enabled = isEnabled,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedPlaceholderColor = defaultPlaceHolderColor,
                unfocusedPlaceholderColor = defaultPlaceHolderColor,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                selectionColors = TextSelectionColors(
                    handleColor = Color.Black,
                    backgroundColor = defaultMyMessageItemColor.copy(alpha = 0.4f)
                )
            ),
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        IconButton(
            enabled = isEnabled,
            onClick = { onSendClick() }) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(defaultMyMessageItemColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = stringResource(R.string.btn_send),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatList(
    modifier: Modifier,
    chatList: List<Chat>,
    currentUserUid: String,
    otherUserUid: String,
    otherUserName: String,
    otherUserImageUrl: String
) {
    val configuration = LocalConfiguration.current
    val screenWidth = remember { configuration.screenWidthDp.dp }

    LazyColumn(modifier) {
        items(chatList.size) { index ->
            if (chatList[index].uid == currentUserUid) {
                ChatItem(
                    text = chatList[index].message,
                    userName = "",
                    userImageUrl = "",
                    chatType = ChatType.ME.ordinal,
                    screenWidth = screenWidth
                )
            } else {
                ChatItem(
                    text = chatList[index].message,
                    userName = otherUserName,
                    userImageUrl = otherUserImageUrl,
                    chatType = ChatType.OTHER.ordinal,
                    screenWidth = screenWidth
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChatItem(
    text: String,
    userName: String,
    userImageUrl: String,
    chatType: Int,
    screenWidth: Dp
) {
    when (chatType) {
        ChatType.ME.ordinal -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = screenWidth * 0.7f)
                        .clip(
                            RoundedCornerShape(
                                topStart = 10.dp,
                                topEnd = 0.dp,
                                bottomStart = 10.dp,
                                bottomEnd = 10.dp
                            )
                        )
                        .background(defaultMyMessageItemColor)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = text, color = Color.White, fontSize = 16.sp)
                }
            }
        }

        ChatType.OTHER.ordinal -> {
            Row(
                modifier = Modifier.widthIn(max = screenWidth * 0.7f),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    model = userImageUrl,
                    contentDescription = stringResource(R.string.user_profile_image),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 10.dp,
                                bottomStart = 10.dp,
                                bottomEnd = 10.dp
                            )
                        )
                        .background(defaultOtherMessageItemColor)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = text, color = Color.Black, fontSize = 16.sp)
                }
            }
        }
    }
}

enum class ChatType {
    ME,
    OTHER
}