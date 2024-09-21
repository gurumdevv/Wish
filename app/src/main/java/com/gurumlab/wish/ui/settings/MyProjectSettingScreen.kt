package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.ui.theme.Gray00
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.theme.defaultScrimColor
import com.gurumlab.wish.ui.util.CustomLottieLoader
import com.gurumlab.wish.ui.util.toDp
import kotlinx.coroutines.launch

@Composable
fun MyProjectSettingScreen(viewModel: MyProjectSettingViewModel) {
    MyProjectSettingContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectSettingContent(
    modifier: Modifier = Modifier,
    viewModel: MyProjectSettingViewModel
) {
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val totalCount = wishes.value.keys.size
    val successCount = wishes.value.values.count { it.status == WishStatus.COMPLETED.ordinal }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentWishId by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isException = viewModel.isException.collectAsStateWithLifecycle()

    if (isLoading.value) {
        MyProjectSettingLoadingScreen()
    } else {
        if (isException.value) {
            MyProjectSettingExceptionScreen()
        } else {
            LazyColumn(
                modifier = modifier
            ) {
                item {
                    MyProjectSettingTitle(textRsc = R.string.my_project_setting, fontSize = 24)
                    Spacer(modifier = Modifier.height(16.dp))
                    MyProjectSettingTitle(textRsc = R.string.statistics, fontSize = 20)
                    Spacer(modifier = Modifier.height(8.dp))
                    MyProjectStatistics(totalCount, successCount)
                    Spacer(modifier = Modifier.height(16.dp))
                    MyProjectSettingTitle(textRsc = R.string.posted_wish_list, fontSize = 20)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(wishes.value.keys.size) { index ->
                    if (wishes.value.values.elementAt(index).status == WishStatus.POSTED.ordinal) {
                        MyProjectItem(
                            wish = wishes.value.values.elementAt(index),
                            wishId = wishes.value.keys.elementAt(index),
                            onClick = { wishId ->
                                currentWishId = wishId
                                showBottomSheet = true
                            })
                    } else {
                        MyProjectItem(
                            wish = wishes.value.values.elementAt(index),
                            wishId = wishes.value.keys.elementAt(index)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            containerColor = defaultBoxColor,
            contentColor = Color.White,
            scrimColor = defaultScrimColor
        ) {
            ModalBottomSheetItem(
                modifier = Modifier.clickable {
                    scope.launch {
                        viewModel.deleteWish(currentWishId)
                        viewModel.loadWishes()
                        sheetState.hide()
                    }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
                iconRsc = R.drawable.ic_trash,
                textRsc = R.string.delete,
                contentDescriptionRsc = R.string.btn_delete,
                color = Color.Red
            )
        }
    }
}

@Composable
fun MyProjectSettingTitle(
    textRsc: Int,
    fontSize: Int
) {
    Text(
        text = stringResource(textRsc),
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun MyProjectStatistics(successWishCount: Int, postedWishCount: Int) {
    var heightPx by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        MyProjectStaticsItem(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 0.dp,
                        bottomStart = 10.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(defaultBoxColor)
                .padding(8.dp)
                .onGloballyPositioned { coordinates ->
                    heightPx = coordinates.size.height
                },
            textRsc = R.string.successWish, count = successWishCount
        )
        VerticalDivider(modifier = Modifier.height(heightPx.toDp()), color = Color.Black)
        MyProjectStaticsItem(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 10.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 10.dp
                    )
                )
                .background(defaultBoxColor)
                .padding(8.dp),
            textRsc = R.string.postedWish, count = postedWishCount
        )
    }
}

@Composable
fun MyProjectStaticsItem(
    modifier: Modifier = Modifier,
    textRsc: Int,
    count: Int,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = textRsc), fontSize = 16.sp, color = Gray00)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun MyProjectItem(
    wish: Wish,
    wishId: String,
    onClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = wish.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = wish.oneLineDescription,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .size(72.dp)
            ) {
                when (wish.status) {
                    WishStatus.ONGOING.ordinal -> {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_ongoing),
                            contentDescription = stringResource(
                                R.string.icon_ongoing
                            ),
                            contentScale = ContentScale.Fit
                        )
                    }

                    WishStatus.COMPLETED.ordinal -> {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_completed),
                            contentDescription = stringResource(
                                R.string.icon_completed
                            ),
                            contentScale = ContentScale.Fit

                        )
                    }
                }
            }
        }

        if (wish.status == WishStatus.POSTED.ordinal) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onClick(wishId) }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_etc),
                    contentDescription = stringResource(
                        R.string.btn_etc
                    )
                )
            }
        }
    }
}

@Composable
fun ModalBottomSheetItem(
    modifier: Modifier = Modifier,
    iconRsc: Int,
    textRsc: Int,
    contentDescriptionRsc: Int,
    color: Color = Color.White
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRsc),
            contentDescription = stringResource(id = contentDescriptionRsc),
            tint = color
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = stringResource(id = textRsc), fontSize = 18.sp, color = color)
    }
}

@Composable
fun MyProjectSettingLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp)
    ) {
        MyProjectSettingTitle(textRsc = R.string.my_project_setting, fontSize = 24)
        CustomLottieLoader(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.Center),
            resId = R.raw.animation_default_loading
        )
    }
}

@Composable
fun MyProjectSettingExceptionScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.cannot_load_data),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}