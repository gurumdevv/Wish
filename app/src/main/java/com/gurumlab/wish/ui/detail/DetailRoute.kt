package com.gurumlab.wish.ui.detail

import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.Wish

@Composable
fun DetailRoute(wish: Wish) {
    DetailScreen(wish = wish)
}