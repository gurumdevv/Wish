package com.gurumlab.wish.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { wishes.value.keys.size })
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isError = viewModel.isError.collectAsStateWithLifecycle()
    val isException = viewModel.isException.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            CustomTopAppBar()

            Box {
                if (isError.value) {
                    HomeErrorScreen(modifier)
                } else if (isException.value) {
                    HomeExceptionScreen(modifier) {
                        viewModel.loadWishes()
                    }
                }

                VerticalPager(state = pagerState) { page ->
                    val wishIdentifier = wishes.value.keys.elementAt(page)
                    val wishContent = wishes.value.values.elementAt(page)
                    WishCard(
                        wish = wishContent,
                        onStartClick = {},
                        onLikeClick = {
                            scope.launch {
                                viewModel.getLikes(wishIdentifier).single()
                                    .let { currentCount ->
                                        if (currentCount == -1) snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.fail_like_update),
                                            duration = SnackbarDuration.Short
                                        )
                                        else viewModel.updateLikeCount(
                                            wishIdentifier,
                                            currentCount + 1
                                        )
                                    }
                            }
                        }
                    )
                }

                if (isLoading.value) {
                    HomeLoadingScreen(modifier)
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
        }
    }
}