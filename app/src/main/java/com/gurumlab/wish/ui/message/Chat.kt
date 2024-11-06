package com.gurumlab.wish.ui.message

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Chat
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.ui.theme.defaultDonationButtonColor
import com.gurumlab.wish.ui.theme.defaultMessageAboutSubmissionItemColor
import com.gurumlab.wish.ui.theme.defaultMessageAboutSubmissionTextColor
import com.gurumlab.wish.ui.theme.defaultMyMessageItemColor
import com.gurumlab.wish.ui.theme.defaultOtherMessageItemColor
import com.gurumlab.wish.ui.theme.defaultPlaceHolderColor
import com.gurumlab.wish.ui.theme.defaultSubmissionCheckButtonColor
import com.gurumlab.wish.ui.util.CustomAsyncImage
import com.gurumlab.wish.ui.util.CustomLottieLoader
import com.gurumlab.wish.ui.util.CustomTextFieldWithCustomPadding
import com.gurumlab.wish.ui.util.DateTimeConverter
import com.gurumlab.wish.ui.util.toDp
import com.valentinilk.shimmer.shimmer

// <-- common -->
@Composable
fun ChatFailScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.cannot_chat_now),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

// <-- Chats -->
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
    othersProfileImageUrl: String,
    othersFcmToken: String,
    context: Context,
    onChatRoom: (ChatRoom, String, String, String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onChatRoom(chatRoom, othersName, othersProfileImageUrl, othersFcmToken)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomAsyncImage(
            url = othersProfileImageUrl,
            contentDescription = stringResource(id = R.string.profile_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp),
            defaultPainterResource = R.drawable.ic_profile
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

@Composable
fun ShimmerChatsItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .shimmer()
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .shimmer()
                    .fillMaxWidth(0.3f)
                    .height(16.sp.toDp())
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .shimmer()
                    .fillMaxWidth(0.7f)
                    .height(16.sp.toDp())
                    .background(Color.LightGray)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun ChatsLoadingScreen() {
    LazyColumn {
        items(count = 7) {
            ShimmerChatsItem()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// <-- ChatRoom -->
@Composable
fun ChatRoomLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomLottieLoader(
            resId = R.raw.animation_loading_circle,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun ChatInput(
    text: String,
    isEnabled: Boolean,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            CustomTextFieldWithCustomPadding(
                value = text,
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
                ),
                contentPadding = PaddingValues(14.dp),
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(
                enabled = isEnabled,
                onClick = onSendClick
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(defaultMyMessageItemColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = stringResource(R.string.btn_send),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ChatList(
    state: LazyListState,
    chatList: List<Chat>,
    currentUserUid: String,
    othersUserName: String,
    othersUserImageUrl: String,
    screenWidth: Dp,
    isReverse: Boolean,
    onRepository: (String) -> Unit,
    onDonation: (String) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        state = state,
        reverseLayout = isReverse,
        modifier = modifier.fillMaxSize()
    ) {
        items(chatList.size) { index ->
            val currentItem = chatList[index]
            if (currentItem.uid == currentUserUid) {
                if (currentItem.submission) {
                    ChatItemAboutSubmission(messageRes = R.string.submission_complete)
                } else {
                    ChatItem(
                        text = currentItem.message,
                        userName = "",
                        userImageUrl = "",
                        chatType = ChatType.Me,
                        screenWidth = screenWidth
                    )
                }
            } else {
                if (currentItem.submission) {
                    ChatItemWithButton(
                        messageRes = R.string.please_donate_to_developer,
                        buttonTextRes = R.string.btn_donation,
                        submissionType = SubmissionType.Donation,
                        screenWidth = screenWidth
                    ) { onDonation(currentItem.message) }
                    Spacer(modifier = Modifier.height(8.dp))
                    ChatItemWithButton(
                        messageRes = R.string.receive_submission_from_developer,
                        buttonTextRes = R.string.btn_check_result,
                        submissionType = SubmissionType.Check,
                        screenWidth = screenWidth
                    ) { onRepository(currentItem.message) }
                    Spacer(modifier = Modifier.height(16.dp))
                    ChatItemAboutSubmission(R.string.submission_received)
                } else {
                    ChatItem(
                        text = currentItem.message,
                        userName = othersUserName,
                        userImageUrl = othersUserImageUrl,
                        chatType = ChatType.Other,
                        screenWidth = screenWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChatItemAboutSubmission(messageRes: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultMessageAboutSubmissionItemColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = messageRes),
            color = defaultMessageAboutSubmissionTextColor,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ChatItemWithButton(
    messageRes: Int,
    buttonTextRes: Int,
    submissionType: SubmissionType,
    screenWidth: Dp,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(screenWidth * 0.6f)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(id = messageRes), fontSize = 12.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = when (submissionType) {
                    SubmissionType.Check -> defaultSubmissionCheckButtonColor
                    SubmissionType.Donation -> defaultDonationButtonColor
                }
            ),
            shape = RoundedCornerShape(10.dp),
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = buttonTextRes),
                fontSize = 12.sp,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 1f
                    )
                )
            )
        }
    }
}

@Composable
fun ChatItem(
    text: String,
    userName: String,
    userImageUrl: String,
    chatType: ChatType,
    screenWidth: Dp,
) {
    when (chatType) {
        ChatType.Me -> {
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

        ChatType.Other -> {
            Row(
                modifier = Modifier.widthIn(max = screenWidth * 0.7f),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                CustomAsyncImage(
                    url = userImageUrl,
                    contentDescription = stringResource(R.string.user_profile_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    defaultPainterResource = R.drawable.ic_profile
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

sealed class ChatType {
    data object Me : ChatType()
    data object Other : ChatType()
}

sealed class SubmissionType {
    data object Check : SubmissionType()
    data object Donation : SubmissionType()
}