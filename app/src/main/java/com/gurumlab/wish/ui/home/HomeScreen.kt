package com.gurumlab.wish.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onDetailScreen: (wishId: String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.loadWishes()
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        HomeContent(
            viewModel = viewModel,
            onDetailScreen = onDetailScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        )
    }
}

@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    onDetailScreen: (wishId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isFailUpdateLikeCount by viewModel.isFailUpdateLikeCount.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when (uiState) {
            HomeUiState.Loading -> {
                HomeLoadingScreen()
            }

            HomeUiState.Empty -> {
                HomeErrorScreen()
            }

            HomeUiState.Exception -> {
                HomeExceptionScreen {
                    viewModel.loadWishes()
                }
            }

            is HomeUiState.Success -> {
                val wishes = (uiState as HomeUiState.Success).wishes
                val pagerState = rememberPagerState(pageCount = { wishes.keys.size })

                HomeVerticalPager(
                    wishes = wishes,
                    pagerState = pagerState,
                    onLikeClick = { wishIdentifier ->
                        scope.launch {
                            handleLikeCount(
                                wishIdentifier = wishIdentifier,
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState,
                                context = context
                            )
                        }
                    },
                    onDetailScreen = onDetailScreen,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
        ) { data ->
            CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
        }
    }

    LaunchedEffect(isFailUpdateLikeCount) {
        if (isFailUpdateLikeCount) {
            showSnackbar(
                snackbarHostState = snackbarHostState,
                message = context.getString(R.string.fail_like_update)
            )
        }
    }
}

private suspend fun handleLikeCount(
    wishIdentifier: String,
    viewModel: HomeViewModel,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    val currentCount = viewModel.getLikes(wishIdentifier).single()
    if (currentCount == -1) {
        showSnackbar(
            snackbarHostState = snackbarHostState,
            message = context.getString(R.string.fail_like_update)
        )
    } else {
        viewModel.updateLikeCount(wishIdentifier, currentCount + 1)
    }
}

private suspend fun showSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    snackbarHostState.showSnackbar(
        message = message,
        duration = duration
    )
}