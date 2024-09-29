package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.Wish

@Composable
fun ProgressForDeveloperRoute(
    minimizedWish: MinimizedWish,
    wishId: String,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    ProgressForDeveloperScreen(
        minimizedWish = minimizedWish,
        wishId = wishId,
        onSubmitScreen = onSubmitScreen,
        onMessageScreen = onMessageScreen
    )
}