package com.gurumlab.wish.ui.wishes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomExceptionScreen

@Composable
fun WishesScreen(
    viewModel: WishesViewModel,
    onDetailScreen: (wishId: String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.loadWishes()
        viewModel.loadWishesSortedByLikes()
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        WishesContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor),
            viewModel = viewModel,
            onDetailScreen = onDetailScreen
        )
    }
}

@Composable
fun WishesContent(
    modifier: Modifier = Modifier,
    viewModel: WishesViewModel,
    onDetailScreen: (wishId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when {
            uiState.isException -> {
                CustomExceptionScreen {
                    viewModel.loadWishes()
                    viewModel.loadWishesSortedByLikes()
                }
            }

            else -> {
                WishesLazyColumn(
                    wishes = uiState.wishes,
                    wishesSortedByLikes = uiState.wishesSortedByLikes,
                    isWishesLoading = uiState.isWishesLoading,
                    isWishesSortedByLikesLoading = uiState.isWishesSortedByLikesLoading,
                    onDetailScreen = onDetailScreen
                )
            }
        }
    }
}