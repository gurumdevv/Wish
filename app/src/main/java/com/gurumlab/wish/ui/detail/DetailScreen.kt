package com.gurumlab.wish.ui.detail

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun DetailScreen(
    wish: Wish,
    wishId: String,
    onProgressScreen: (Wish, String) -> Unit,
    onMessageScreen: (Wish) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        DetailContent(
            scrollState = scrollState,
            wish = wish,
            wishId = wishId,
            onProgressScreen = onProgressScreen,
            onMessageScreen = onMessageScreen
        )
    }
}

@Composable
fun DetailContent(
    scrollState: ScrollState,
    wish: Wish,
    wishId: String,
    onProgressScreen: (Wish, String) -> Unit,
    onMessageScreen: (Wish) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ProjectDescriptionArea(scrollState, wish)
        DetailScreenButtonArea(
            modifier = Modifier.align(Alignment.BottomCenter),
            wish = wish,
            wishId = wishId,
            onProgressScreen = onProgressScreen,
            onMessageScreen = onMessageScreen
        )
    }
}