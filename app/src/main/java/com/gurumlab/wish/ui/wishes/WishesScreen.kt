package com.gurumlab.wish.ui.wishes

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.gurumlab.wish.ui.home.HomeLoadingScreen
import com.gurumlab.wish.ui.theme.White00
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomTopAppBar
import kotlin.random.Random

@Composable
fun WishesScreen(viewModel: WishesViewModel) {
    WishesContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        viewModel = viewModel
    )
}

@Composable
fun WishesContent(
    modifier: Modifier = Modifier,
    viewModel: WishesViewModel
) {
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val wishesSortedByLikes = viewModel.wishesSortedByLikes.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isError = viewModel.isError.collectAsStateWithLifecycle()
    val isException = viewModel.isException.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        CustomTopAppBar()

        if (isLoading.value) {
            HomeLoadingScreen(modifier)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
//                    WishesHeader(wishes.value)
                    Spacer(modifier = Modifier.height(16.dp))
                    WishesSortByLikes(wishesSortedByLikes.value)
                    Spacer(modifier = Modifier.height(16.dp))
                    WishesRandomTitle()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(wishes.value.keys.size) { index ->
                    WishesRandomItem(wish = wishes.value.values.elementAt(index))
                }
            }
        }
    }
}

@Composable
fun WishesHeader(wishes: Map<String, Wish>) {
    val wishesCount = wishes.keys.size
    val randomNumber = if (wishesCount > 0) Random.nextInt(wishesCount) else 0
    val selectedWish = wishes.values.elementAt(randomNumber) // 없는 경우 표시할 헤더 데이터

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = selectedWish.representativeImage),
            contentDescription = stringResource(R.string.wishes_screen_header),
            contentScale = ContentScale.Crop
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
                text = selectedWish.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = selectedWish.comment,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WishesSortByLikes(wishes: List<Wish>) {
    Log.d("WishesScreen", "wishes: ${wishes.size}")
    Column {
        Text(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp),
            text = stringResource(R.string.wishes_sort_by_like_count_title),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(wishes.size) { index ->
                val paddingValue = if (index == 0) 24 else 16
                WishesSortByLikeCountItem(wish = wishes[index], paddingValue)
            }
        }
    }
}

@Composable
fun WishesSortByLikeCountItem(wish: Wish, paddingValue: Int) {
    Column(
        modifier = Modifier.padding(start = paddingValue.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(135.dp),
            painter = painterResource(id = wish.representativeImage),
            contentDescription = stringResource(R.string.wish_image),
            contentScale = ContentScale.Crop
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
fun WishesRandomItem(wish: Wish) {
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
                modifier = Modifier.weight(0.8f),
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = wish.oneLineDescription,
                    color = Color.Black,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                modifier = Modifier.weight(0.2f),
                painter = painterResource(id = wish.representativeImage),
                contentDescription = stringResource(R.string.wish_image),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}