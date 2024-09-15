package com.gurumlab.wish.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomTopAppBar

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    HomeContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        viewModel = viewModel
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { wishes.value.keys.size })

    Column(modifier = modifier) {
        CustomTopAppBar()

        VerticalPager(state = pagerState) { page ->
            WishCard(
                wish = wishes.value.values.elementAt(page),
                onStartClick = {},
                onLikeClick = {}
            )
        }
    }
}

data class TempWish(
    val representativeImage: Int,
    val title: String,
    val oneLineDescription: String,
    val simpleDescription: String,
    val review: String,
)