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
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun ProgressForDeveloperScreen(
    minimizedWish: MinimizedWish,
    wishId: String,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ProgressForDeveloperContent(
            minimizedWish = minimizedWish,
            wishId = wishId,
            scrollState = scrollState,
            onSubmitScreen = onSubmitScreen,
            onMessageScreen = onMessageScreen
        )
    }
}

@Composable
fun ProgressForDeveloperContent(
    minimizedWish: MinimizedWish,
    wishId: String,
    scrollState: ScrollState,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ProjectProgressDescriptionArea(minimizedWish, scrollState)
        ProgressForDeveloperScreenButtonArea(
            modifier = Modifier.align(Alignment.BottomCenter),
            minimizedWish = minimizedWish,
            wishId = wishId,
            onSubmitScreen = onSubmitScreen,
            onMessageScreen = onMessageScreen
        )
    }
}