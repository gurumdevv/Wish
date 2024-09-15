package com.gurumlab.wish.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomTopAppBar
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { wishes.value.keys.size })

    Column(modifier = modifier) {
        CustomTopAppBar()

        VerticalPager(state = pagerState) { page ->
            val wishIdentifier = wishes.value.keys.elementAt(page)
            val wishContent = wishes.value.values.elementAt(page)
            WishCard(
                wish = wishContent,
                onStartClick = {},
                onLikeClick = {
                    scope.launch {
                        viewModel.getLikes(wishIdentifier).single().let { currentCount ->
                            if (currentCount == -1) //TODO("업데이트 실패 토스트 띄우기")
                            else viewModel.updateLikeCount(wishIdentifier, currentCount + 1)
                        }
                    }
                }
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