package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSubmitRoute(
    wishId: String,
    minimizedWish: MinimizedWish,
    onNavUp: () -> Unit,
    onComplete: () -> Unit
) {
    val viewModel = hiltViewModel<ProjectSubmitViewModel>()
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    ProjectSubmitScreen(
        topBar = topBar,
        viewModel = viewModel,
        wishId = wishId,
        minimizedWish = minimizedWish,
        onComplete = onComplete
    )
}