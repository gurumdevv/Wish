package com.gurumlab.wish.ui.wishes

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
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.home.TempWish
import com.gurumlab.wish.ui.theme.White00
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun WishesScreen() {
    Scaffold { innerPadding ->
        WishesContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        )
    }
}

@Composable
fun WishesContent(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            WishesHeader(tempHeaderData)
            Spacer(modifier = Modifier.height(16.dp))
            WishesSortByLikeCount()
            Spacer(modifier = Modifier.height(16.dp))
            WishesRandomTitle()
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(wishList.size) { index ->
            WishesRandomItem(wish = wishList[index])
        }
    }
}

@Preview
@Composable
fun WishesContentPreview() {
    WishesContent()
}

@Composable
fun WishesHeader(wish: TempWish) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = wish.representativeImage),
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
                text = wish.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = wish.review,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun WishesHeaderPreview() {
    WishesHeader(tempHeaderData)
}

@Composable
fun WishesSortByLikeCount() {
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
            items(wishList.size) { index ->
                val paddingValue = if (index == 0) 24 else 16
                WishesSortByLikeCountItem(wish = wishList[index], paddingValue)
            }
        }
    }
}

@Preview
@Composable
fun WishesSortByLikeCountPreview() {
    WishesSortByLikeCount()
}

@Composable
fun WishesSortByLikeCountItem(wish: TempWish, paddingValue: Int) {
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
fun WishesRandomItem(wish: TempWish) {
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

val wishList = listOf(
    TempWish(
        R.drawable.sample_wish_image,
        "wish1",
        "oneLineDescription1",
        "simpleDescription1",
        ""
    ),
    TempWish(
        R.drawable.sample_wish_image,
        "wish2wish2wish2wish2wish2wish2wish2wish2wish2wish2wish2wish2wish2wish2wish2",
        "oneLineDescription2",
        "simpleDescription2",
        ""
    ),
    TempWish(
        R.drawable.sample_wish_image,
        "wish3",
        "oneLineDescription3",
        "simpleDescription3",
        ""
    ),
    TempWish(
        R.drawable.sample_wish_image,
        "wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4wish4",
        "oneLineDescription4",
        "simpleDescription4",
        ""
    ),
    TempWish(
        R.drawable.sample_wish_image,
        "wish5",
        "oneLineDescription5",
        "simpleDescription5",
        ""
    )
    //TODO("나중에 서버에서 데이터를 불러오도록 구현하기")
)

val tempHeaderData = TempWish(
    R.drawable.sample_wishes_header,
    "독서누리",
    "oneLineDescription",
    "simpleDescription",
    "저희 도서관 회원들끼리 지식 나눔 공유가 가능해졌어요."
)