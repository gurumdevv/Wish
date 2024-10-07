package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun ProgressForDeveloperScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    minimizedWish: MinimizedWish,
    wishId: String,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        ProgressForDeveloperContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding),
            minimizedWish = minimizedWish,
            wishId = wishId,
            onSubmitScreen = onSubmitScreen,
            onMessageScreen = onMessageScreen
        )
    }
}

@Composable
fun ProgressForDeveloperContent(
    modifier: Modifier = Modifier,
    minimizedWish: MinimizedWish,
    wishId: String,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
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