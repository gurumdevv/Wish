package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressForDeveloperRoute(
    minimizedWish: MinimizedWish,
    wishId: String,
    onNavUp: () -> Unit,
    onSubmitScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    ProgressForDeveloperScreen(
        topBar = topBar,
        minimizedWish = minimizedWish,
        wishId = wishId,
        onSubmitScreen = onSubmitScreen,
        onMessageScreen = onMessageScreen
    )
}