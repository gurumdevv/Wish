package com.gurumlab.wish.ui.wishes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomExceptionScreen

@Composable
fun WishesScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    viewModel: WishesViewModel,
    onDetailScreen: (wishId: String) -> Unit
) {
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
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val currentUiState = uiState.value

    Box(modifier = modifier) {
        when {
            currentUiState.isException -> {
                CustomExceptionScreen {
                    viewModel.loadWishes()
                    viewModel.loadWishesSortedByLikes()
                }
            }

            else -> {
                WishesLazyColumn(
                    wishes = currentUiState.wishes,
                    wishesSortedByLikes = currentUiState.wishesSortedByLikes,
                    isWishesLoading = currentUiState.isWishesLoading,
                    isWishesSortedByLikesLoading = currentUiState.isWishesSortedByLikesLoading,
                    onDetailScreen = onDetailScreen
                )
            }
        }
    }
}

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
            items(wishes.keys.size) { index ->
                WishesRandomItem(
                    wish = wishes.values.elementAt(index),
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