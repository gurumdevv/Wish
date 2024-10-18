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
    wishId: String,
    minimizedWish: MinimizedWish,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        ProgressForDeveloperContent(
            wishId = wishId,
            minimizedWish = minimizedWish,
            onSubmitScreen = onSubmitScreen,
            onMessageScreen = onMessageScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        )
    }
}

@Composable
fun ProgressForDeveloperContent(
    wishId: String,
    minimizedWish: MinimizedWish,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
    ) {
        ProjectProgressDescriptionArea(minimizedWish, scrollState)
        if (minimizedWish.completedDate == 0) {
            ProgressForDeveloperScreenButtonArea(
                onSubmitScreen = { onSubmitScreen(minimizedWish, wishId) },
                onMessageScreen = { onMessageScreen(minimizedWish) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}