package com.gurumlab.wish.ui.home

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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.White00
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomIconButton
import com.gurumlab.wish.ui.util.CustomTopAppBar

@Composable
fun HomeScreen() {
    Scaffold { innerPadding ->
        HomeContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { wishList.size })

    Column(modifier = modifier) {
        CustomTopAppBar()

        VerticalPager(state = pagerState) { page ->
            WishCard(wish = wishList[page])
        }
    }
}

@Composable
fun WishCard(
    modifier: Modifier = Modifier,
    wish: TempWish
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(modifier = Modifier) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                painter = painterResource(id = wish.representativeImage),
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
        Spacer(modifier = Modifier.height(16.dp))
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
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomIconButton(
                text = stringResource(R.string.start),
                icon = R.drawable.ic_magic,
                description = stringResource(R.string.btn_start),
                onClick = {})
            CustomIconButton(
                text = stringResource(R.string.like),
                icon = R.drawable.ic_like,
                description = stringResource(R.string.btn_like),
                onClick = {})
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class TempWish(
    val representativeImage: Int,
    val title: String,
    val oneLineDescription: String,
    val simpleDescription: String,
    val review: String,
)

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
        "wish2",
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
    )
    //TODO("나중에 서버에서 데이터를 불러오도록 구현하기")
)