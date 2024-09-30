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
import com.gurumlab.wish.ui.util.CustomLottieLoader
import com.gurumlab.wish.ui.util.toDp

@Composable
fun ApproachingProjectSettingScreen(viewModel: SettingsViewModel) {
    viewModel.loadApproachingWishes()

    ApproachingProjectSettingContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel
    )
}

@Composable
fun ApproachingProjectSettingContent(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val wishes = viewModel.approachingWishes.collectAsStateWithLifecycle()
    val totalCount = wishes.value.keys.size
    val successCount = wishes.value.values.count { it.status == WishStatus.COMPLETED.ordinal }
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isException = viewModel.isException.collectAsStateWithLifecycle()

    if (isLoading.value) {
        ApproachingProjectSettingLoadingScreen()
    } else {
        if (isException.value) {
            ApproachingProjectSettingExceptionScreen()
        } else {
            LazyColumn(
                modifier = modifier
            ) {
                item {
                    ApproachingProjectSettingTitle(
                        textRsc = R.string.approaching_project_setting,
                        fontSize = 24
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ApproachingProjectSettingTitle(textRsc = R.string.statistics, fontSize = 20)
                    Spacer(modifier = Modifier.height(8.dp))
                    ApproachingProjectStatistics(totalCount, successCount)
                    Spacer(modifier = Modifier.height(16.dp))
                    ApproachingProjectSettingTitle(
                        textRsc = R.string.posted_wish_list,
                        fontSize = 20
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(wishes.value.keys.size) { index ->
                    ApproachingProjectItem(wish = wishes.value.values.elementAt(index))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ApproachingProjectSettingTitle(
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
fun ApproachingProjectStatistics(successWishCount: Int, postedWishCount: Int) {
    var heightPx by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        ApproachingProjectStaticsItem(
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
        ApproachingProjectStaticsItem(
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
fun ApproachingProjectStaticsItem(
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
fun ApproachingProjectItem(wish: Wish) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
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
}

@Composable
fun ApproachingProjectSettingLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp)
    ) {
        MyProjectSettingTitle(textRsc = R.string.approaching_project_setting, fontSize = 24)
        CustomLottieLoader(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.Center),
            resId = R.raw.animation_default_loading
        )
    }
}

@Composable
fun ApproachingProjectSettingExceptionScreen() {
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