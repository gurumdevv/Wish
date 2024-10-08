package com.gurumlab.wish.ui.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.gurumlab.wish.ui.util.CustomExceptionScreen
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
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val currentUiState = uiState.value
    val pagerState = rememberPagerState(pageCount = { currentUiState.wishes.keys.size })

    Box(modifier = modifier) {
        when {
            currentUiState.isError || currentUiState.isEmpty -> {
                HomeErrorScreen()
            }

            currentUiState.isException -> {
                CustomExceptionScreen {
                    viewModel.loadWishes()
                }
            }

            else -> {
                VerticalPager(state = pagerState) { page ->
                    val wishIdentifier = currentUiState.wishes.keys.elementAt(page)
                    val wishContent = currentUiState.wishes.values.elementAt(page)
                    WishCard(
                        wish = wishContent,
                        onStartClick = { onDetailScreen(wishIdentifier) },
                        onLikeClick = {
                            scope.launch {
                                handleLikeClick(
                                    viewModel = viewModel,
                                    wishIdentifier = wishIdentifier,
                                    context = context,
                                    snackbarHostState = snackbarHostState
                                )
                            }
                        }
                    )
                }
            }
        }

        if (currentUiState.isLoading) {
            HomeLoadingScreen()
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

    LaunchedEffect(currentUiState.isFailUpdateLikeCount) {
        if (currentUiState.isFailUpdateLikeCount) {
            showSnackbar(
                snackbarHostState = snackbarHostState,
                message = context.getString(R.string.fail_like_update)
            )
        }
    }
}

private suspend fun handleLikeClick(
    viewModel: HomeViewModel,
    wishIdentifier: String,
    context: Context,
    snackbarHostState: SnackbarHostState,
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