package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.ui.theme.Gray00
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.toDp

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

@Composable
fun MyProjectSettingContent(
    modifier: Modifier = Modifier,
    viewModel: MyProjectSettingViewModel
) {
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val totalCount = wishes.value.keys.size
    val successCount = wishes.value.values.count { it.status == WishStatus.COMPLETED.ordinal }

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
                    onClick = {
                        //TODO("제어 패널 띄우기")
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
        Text(text = stringResource(id = textRsc), fontSize = 18.sp, color = Gray00)
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
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Box(
                modifier = Modifier
                    .size(94.dp)
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