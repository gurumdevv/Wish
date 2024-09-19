package com.gurumlab.wish.ui.detail

import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.Wish

@Composable
fun DetailRoute(
    wish: Wish,
    wishId: String,
    onProgressScreen: (Wish, String) -> Unit,
    onMessageScreen: (Wish) -> Unit
) {
    DetailScreen(
        wish = wish,
        wishId = wishId,
        onProgressScreen = onProgressScreen,
        onMessageScreen = onMessageScreen
    )
}