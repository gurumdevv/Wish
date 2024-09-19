package com.gurumlab.wish.ui.progressForDeveloper

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
fun ProgressForDeveloperScreen(
    wish: Wish,
    wishId: String,
    onSubmitScreen: (Wish, String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ProgressForDeveloperContent(
            wish = wish,
            wishId = wishId,
            scrollState = scrollState,
            onSubmitScreen = { wish, wishId ->
                onSubmitScreen(wish, wishId) },
            onMessageScreen = {}
        )
    }
}

@Composable
fun ProgressForDeveloperContent(
    wish: Wish,
    wishId: String,
    scrollState: ScrollState,
    onSubmitScreen: (Wish, String) -> Unit,
    onMessageScreen: (Wish) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ProjectProgressDescriptionArea(wish, scrollState)
        ProgressForDeveloperScreenButtonArea(
            modifier = Modifier.align(Alignment.BottomCenter),
            wish = wish,
            wishId = wishId,
            onSubmitScreen = { wish, wishId ->
                onSubmitScreen(wish, wishId) },
            onMessageScreen = { onMessageScreen(wish) }
        )
    }
}