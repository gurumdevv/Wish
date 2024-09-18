package com.gurumlab.wish.ui.detail

import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.Wish

@Composable
fun DetailRoute(
    wish: Wish,
    onProgressScreen: (Wish) -> Unit,
    onMessageScreen: (Wish) -> Unit
) {
    DetailScreen(
        wish = wish,
        onProgressScreen = onProgressScreen,
        onMessageScreen = onMessageScreen
    )
}