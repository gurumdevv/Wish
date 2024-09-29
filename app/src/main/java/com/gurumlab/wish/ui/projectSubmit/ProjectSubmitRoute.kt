package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.MinimizedWish

@Composable
fun ProjectSubmitRoute(minimizedWish: MinimizedWish, wishId: String, onComplete: () -> Unit) {
    val viewModel = hiltViewModel<ProjectSubmitViewModel>()
    ProjectSubmitScreen(
        viewModel = viewModel,
        minimizedWish = minimizedWish,
        wishId = wishId,
        onComplete = onComplete
    )
}