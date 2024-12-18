package com.gurumlab.wish.ui.wishes

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.White00
import com.gurumlab.wish.ui.util.CustomAsyncImage
import com.gurumlab.wish.ui.util.URL
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun WishesLazyColumn(
    wishes: Map<String, Wish>,
    wishesSortedByLikes: Map<String, Wish>,
    isWishesLoading: Boolean,
    isWishesSortedByLikesLoading: Boolean,
    onDetailScreen: (wishId: String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            WishesBannerSection(isLoading = isWishesLoading, wishes = wishes)
            Spacer(modifier = Modifier.height(16.dp))
            WishesSortByLikesTitle()
            Spacer(modifier = Modifier.height(16.dp))
            WishesSortByLikesSection(
                isLoading = isWishesSortedByLikesLoading,
                wishesSortedByLikes = wishesSortedByLikes,
                onDetailScreen = onDetailScreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            WishesRandomTitle()
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isWishesLoading) {
            items(15) { ShimmerWishesRandomItem() }
        } else {
            itemsIndexed(
                items = wishes.values.toList(),
                key = { index, _ -> wishes.keys.elementAt(index) }) { index, wish ->
                WishesRandomItem(
                    wish = wish,
                    wishId = wishes.keys.elementAt(index),
                    onDetailScreen = onDetailScreen
                )
            }
        }
    }
}

@Composable
fun WishesBannerSection(isLoading: Boolean, wishes: Map<String, Wish>) {
    if (isLoading) {
        ShimmerWishesBanner()
    } else {
        WishesBanner(wishes)
    }
}

@Composable
fun WishesSortByLikesSection(
    isLoading: Boolean,
    wishesSortedByLikes: Map<String, Wish>,
    onDetailScreen: (wishId: String) -> Unit
) {
    if (isLoading) {
        ShimmerWishesSortByLikes()
    } else {
        WishesSortByLikes(wishesSortedByLikes, onDetailScreen)
    }
}

@Composable
fun WishesBanner(wishes: Map<String, Wish>) {
    val defaultWish = getDefaultWish(
        title = stringResource(R.string.wish),
        comment = stringResource(R.string.please_wishes_come_true)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        CustomAsyncImage(
            url = defaultWish.representativeImage,
            contentDescription = stringResource(R.string.wishes_screen_header),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(White00, Color.Black)
                    )
                )
                .align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, end = 24.dp)
        ) {
            Text(
                text = defaultWish.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = defaultWish.comment,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ShimmerWishesBanner() {
    Box(
        modifier = Modifier
            .shimmer()
            .fillMaxWidth()
            .height(260.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun WishesSortByLikesTitle() {
    Text(
        modifier = Modifier.padding(start = 24.dp, end = 24.dp),
        text = stringResource(R.string.wishes_sort_by_like_count_title),
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun WishesSortByLikes(
    wishes: Map<String, Wish>,
    onDetailScreen: (wishId: String) -> Unit
) {
    LazyRow {
        items(wishes.size) { index ->
            val paddingValue = if (index == 0) 24 else 16
            WishesSortByLikesItem(
                wish = wishes.values.elementAt(index),
                wishId = wishes.keys.elementAt(index),
                paddingValue = paddingValue,
                onDetailScreen = onDetailScreen
            )
        }
    }
}

@Composable
fun WishesSortByLikesItem(
    wish: Wish,
    wishId: String,
    paddingValue: Int,
    onDetailScreen: (wishId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(start = paddingValue.dp)
            .clickable { onDetailScreen(wishId) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomAsyncImage(
            url = wish.representativeImage,
            contentDescription = stringResource(R.string.wish_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(135.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.width(135.dp),
            text = wish.title,
            color = Color.White,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ShimmerWishesSortByLikes() {
    Row {
        repeat(10) { index ->
            val paddingValue = if (index == 0) 24 else 16
            ShimmerWishesSortByLikesItem(paddingValue = paddingValue)
        }
    }
}

@Composable
fun ShimmerWishesSortByLikesItem(paddingValue: Int) {
    Column(
        modifier = Modifier.padding(start = paddingValue.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .shimmer()
                .size(135.dp)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .shimmer()
                .width(135.dp)
                .height(16.dp)
                .background(Color.LightGray)
        )
    }
}

@Composable
fun WishesRandomTitle() {
    Text(
        modifier = Modifier.padding(start = 24.dp),
        text = stringResource(R.string.wishes_random_title),
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun WishesRandomItem(
    wish: Wish,
    wishId: String,
    onDetailScreen: (wishId: String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
                .clickable { onDetailScreen(wishId) }
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = wish.title,
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wish.simpleDescription,
                    color = Color.Black,
                    fontSize = 14.sp,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            CustomAsyncImage(
                url = wish.representativeImage,
                contentDescription = stringResource(R.string.wish_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(78.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ShimmerWishesRandomItem() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    Column {
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .shimmer(shimmerInstance)
                        .width(170.dp)
                        .height(16.dp)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .shimmer(shimmerInstance)
                        .width(120.dp)
                        .height(16.dp)
                        .background(Color.LightGray)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .shimmer(shimmerInstance)
                    .size(78.dp)
                    .background(Color.LightGray)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

fun getDefaultWish(title: String, comment: String): Wish {
    return Wish(
        postId = "",
        createdDate = 0,
        startedDate = 0,
        completedDate = 0,
        posterId = "",
        developerId = "",
        posterName = "",
        developerName = "",
        title = title,
        representativeImage = URL.DEFAULT_WISH_PHOTO,
        status = 0,
        likes = 0,
        oneLineDescription = "",
        simpleDescription = "",
        detailDescription = "",
        detailFeatures = emptyList(),
        features = emptyList(),
        comment = comment
    )
}