package com.gurumlab.wish.ui.detail

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRoute(
    wishId: String,
    onNavUp: () -> Unit,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    val viewModel = hiltViewModel<DetailViewModel>()
    DetailScreen(
        wishId = wishId,
        viewModel = viewModel,
        onProgressScreen = onProgressScreen,
        onMessageScreen = onMessageScreen,
        topBar = topBar
    )
}