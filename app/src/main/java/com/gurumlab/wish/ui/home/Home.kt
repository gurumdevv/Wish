package com.gurumlab.wish.ui.home

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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.White00
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomExceptionScreen
import com.gurumlab.wish.ui.util.CustomIconButton
import com.gurumlab.wish.ui.util.CustomLottieLoader

@Composable
fun HomeVerticalPager(
    wishes: Map<String, Wish>,
    pagerState: PagerState,
    onLikeClick: (String) -> Unit,
    onDetailScreen: (wishId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        VerticalPager(state = pagerState) { page ->
            val wishIdentifier = wishes.keys.elementAt(page)
            val wishContent = wishes.values.elementAt(page)
            WishCard(
                wish = wishContent,
                onStartClick = { onDetailScreen(wishIdentifier) },
                onLikeClick = { onLikeClick(wishIdentifier) }
            )
        }
    }
}

@Composable
fun WishCard(
    modifier: Modifier = Modifier,
    wish: Wish,
    onStartClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    var screenHeight by remember { mutableIntStateOf(0) }
    var noImageContentHeight by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .onGloballyPositioned { coordinates ->
                screenHeight = coordinates.size.height
            }
    ) {
        WishCardImageArea(wish, screenHeight, noImageContentHeight)
        WishCardContentWithoutImageArea(wish, onStartClick, onLikeClick) { height ->
            noImageContentHeight = height
        }
    }
}

@Composable
fun WishCardImageArea(
    wish: Wish,
    screenHeight: Int,
    noImageContentHeight: Int
) {
    val density = LocalDensity.current

    Box {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(density) { (screenHeight - noImageContentHeight).toDp() }),
            model = wish.representativeImage,
            contentDescription = stringResource(R.string.wish_representative_image),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(87.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(White00, Color.Black)
                    )
                )
                .align(Alignment.BottomCenter)
        )
        Text(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .align(Alignment.BottomStart),
            text = wish.title,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Black, offset = Offset(0f, 4.0f), blurRadius = 4f
                )
            )
        )
    }
}

@Composable
fun WishCardContentWithoutImageArea(
    wish: Wish,
    onStartClick: () -> Unit,
    onLikeClick: () -> Unit,
    onHeightChange: (height: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .onGloballyPositioned { coordinates -> onHeightChange(coordinates.size.height) }
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        WishCardDescriptionArea(wish)
        Spacer(modifier = Modifier.height(16.dp))
        WishCardButtonArea(onStartClick, onLikeClick)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun WishCardDescriptionArea(wish: Wish) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
            text = "\"${wish.oneLineDescription}\"",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            text = wish.simpleDescription,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun WishCardButtonArea(
    onStartClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CustomIconButton(
            text = stringResource(R.string.start),
            icon = R.drawable.ic_magic,
            description = stringResource(R.string.btn_start),
            onClick = { onStartClick() })
        CustomIconButton(
            text = stringResource(R.string.like),
            icon = R.drawable.ic_like,
            description = stringResource(R.string.btn_like),
            onClick = { onLikeClick() })
    }
}

@Composable
fun HomeLoadingScreen(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val lottieHeight = (configuration.screenHeightDp / 3).dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        CustomLottieLoader(
            modifier = Modifier
                .fillMaxWidth()
                .height(lottieHeight),
            resId = R.raw.animation_shooting_star
        )
    }
}

@Composable
fun HomeErrorScreen(modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val lottieHeight = (configuration.screenHeightDp / 3).dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
    ) {
        CustomLottieLoader(
            modifier = Modifier
                .fillMaxWidth()
                .height(lottieHeight),
            resId = R.raw.animation_shooting_star
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_wish),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.how_about_sharing_wish),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun HomeExceptionScreen(modifier: Modifier = Modifier, onClick: () -> Unit) {
    CustomExceptionScreen(modifier = modifier, onClick = onClick)
}