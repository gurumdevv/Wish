package com.gurumlab.wish.ui.detail

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.MinimizedWish

@Composable
fun DetailRoute(
    wishId: String,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val viewModel = hiltViewModel<DetailViewModel>()
    DetailScreen(
        wishId = wishId,
        viewModel = viewModel,
        onProgressScreen = onProgressScreen,
        onMessageScreen = onMessageScreen
    )
}