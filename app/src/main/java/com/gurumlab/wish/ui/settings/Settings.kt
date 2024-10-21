package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.ui.theme.Gray00
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.theme.defaultScrimColor
import com.gurumlab.wish.ui.util.CustomAsyncImage
import com.gurumlab.wish.ui.util.CustomLottieLoader

//<--- Settings--->
@Composable
fun SettingsUserInfo(currentUserInfo: CurrentUserInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = currentUserInfo.name,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentUserInfo.email,
                fontSize = 16.sp,
                color = Color.White,
            )
        }
        CustomAsyncImage(
            url = currentUserInfo.imageUrl,
            contentDescription = stringResource(R.string.profile_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            defaultPainterResource = R.drawable.ic_profile
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    shape: RoundedCornerShape
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .clip(shape)
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White,
        )
    }
}

//<--- Account --->
@Composable
fun AccountSettingTitle() {
    Text(
        text = stringResource(R.string.account_setting),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun AccountSubsetTitle(
    textRsc: Int
) {
    Text(
        text = stringResource(textRsc),
        fontSize = 18.sp,
        color = Color.White
    )
}

@Composable
fun AccountSubsetButton(
    textRsc: Int,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = defaultBoxColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick
    ) {
        Row {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(textRsc),
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

//<--- ProjectSettingComponents --->
@Composable
fun ProjectSettingTitle(
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
fun ProjectStatistics(successWishCount: Int, postedWishCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        ProjectStatisticsItem(
            textRsc = R.string.successWish,
            count = successWishCount,
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
        )
        VerticalDivider(modifier = Modifier.fillMaxHeight(), color = Color.Black)
        ProjectStatisticsItem(
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
fun ProjectStatisticsItem(
    textRsc: Int,
    count: Int,
    modifier: Modifier = Modifier,
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
fun ProjectListItem(
    wish: Wish,
    wishId: String,
    getMinimizedWish: ((Wish) -> MinimizedWish)? = null,
    onProgressScreen: ((MinimizedWish, String) -> Unit)? = null,
    onDetailScreen: ((String) -> Unit)? = null,
    onOptionClick: ((String) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .clickable {
                onDetailScreen?.let { onDetailScreen(wishId) }
                onProgressScreen?.let {
                    val minimizedWish = getMinimizedWish!!(wish)
                    onProgressScreen(minimizedWish, wishId)
                }
            }
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

        if (wish.status == WishStatus.POSTED.ordinal && onOptionClick != null) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onOptionClick(wishId) }) {
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
fun ProjectSettingLoadingScreen(
    textRsc: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        ProjectSettingTitle(textRsc = textRsc, fontSize = 24)
        CustomLottieLoader(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.Center),
            resId = R.raw.animation_default_loading
        )
    }
}


@Composable
fun ProjectExceptionScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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

//<--- ApproachingProject --->
@Composable
fun ApproachingProjectWishesList(
    wishes: Map<String, Wish>,
    totalCount: Int,
    successCount: Int,
    getMinimizedWish: ((Wish) -> MinimizedWish)?,
    onProgressScreen: ((MinimizedWish, String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            ProjectSettingTitle(
                textRsc = R.string.approaching_project_setting,
                fontSize = 24
            )
            Spacer(modifier = Modifier.height(16.dp))
            ProjectSettingTitle(textRsc = R.string.statistics, fontSize = 20)
            Spacer(modifier = Modifier.height(8.dp))
            ProjectStatistics(successWishCount = successCount, postedWishCount = totalCount)
            Spacer(modifier = Modifier.height(16.dp))
            ProjectSettingTitle(
                textRsc = R.string.posted_wish_list,
                fontSize = 20
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(wishes.keys.size) { index ->
            ProjectListItem(
                wish = wishes.values.elementAt(index),
                wishId = wishes.keys.elementAt(index),
                getMinimizedWish = getMinimizedWish,
                onProgressScreen = onProgressScreen
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ApproachingProjectLoadingScreen(modifier: Modifier) {
    ProjectSettingLoadingScreen(textRsc = R.string.approaching_project_setting, modifier = modifier)
}

//<--- MyProject --->
@Composable
fun MyProjectWishesList(
    wishes: Map<String, Wish>,
    totalCount: Int,
    successCount: Int,
    onDetailScreen: (String) -> Unit,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        item {
            ProjectSettingTitle(textRsc = R.string.my_project_setting, fontSize = 24)
            Spacer(modifier = Modifier.height(16.dp))
            ProjectSettingTitle(textRsc = R.string.statistics, fontSize = 20)
            Spacer(modifier = Modifier.height(8.dp))
            ProjectStatistics(successWishCount = successCount, postedWishCount = totalCount)
            Spacer(modifier = Modifier.height(16.dp))
            ProjectSettingTitle(textRsc = R.string.posted_wish_list, fontSize = 20)
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(wishes.keys.size) { index ->
            ProjectListItem(
                wish = wishes.values.elementAt(index),
                wishId = wishes.keys.elementAt(index),
                onDetailScreen = onDetailScreen,
                onOptionClick = onOptionClick
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyProjectBottomSheet(
    sheetState: SheetState,
    onDismiss: (Boolean) -> Unit,
    onDeleteBtnClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss(false)
        },
        sheetState = sheetState,
        containerColor = defaultBoxColor,
        contentColor = Color.White,
        scrimColor = defaultScrimColor
    ) {
        ModalBottomSheetItem(
            modifier = Modifier.clickable { onDeleteBtnClick() },
            iconRsc = R.drawable.ic_trash,
            textRsc = R.string.delete,
            contentDescriptionRsc = R.string.btn_delete,
            color = Color.Red
        )
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
fun MyProjectLoadingScreen(modifier: Modifier) {
    ProjectSettingLoadingScreen(textRsc = R.string.my_project_setting, modifier = modifier)
}

//<-- TermsAndCondition --->

@Composable
fun TermsAndConditionTitle(
) {
    Text(
        text = stringResource(R.string.terms_and_condition),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun TermsAndConditionItem(
    textRsc: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(textRsc),
            fontSize = 18.sp,
            color = Color.White
        )
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = stringResource(
                R.string.terms_and_condition_more_info
            ),
            tint = Color.White
        )
    }
}