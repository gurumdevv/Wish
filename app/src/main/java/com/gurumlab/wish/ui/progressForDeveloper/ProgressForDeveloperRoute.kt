package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressForDeveloperRoute(
    wishId: String,
    minimizedWish: MinimizedWish,
    onNavUp: () -> Unit,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(onNavIconPressed = onNavUp)
    }
    ProgressForDeveloperScreen(
        wishId = wishId,
        minimizedWish = minimizedWish,
        onSubmitScreen = onSubmitScreen,
        onMessageScreen = onMessageScreen,
        topBar = topBar
    )
}