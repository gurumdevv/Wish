package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.MinimizedWish

@Composable
fun ProjectSubmitRoute(wishId: String, minimizedWish: MinimizedWish, onComplete: () -> Unit) {
    val viewModel = hiltViewModel<ProjectSubmitViewModel>()
    ProjectSubmitScreen(
        viewModel = viewModel,
        wishId = wishId,
        minimizedWish = minimizedWish,
        onComplete = onComplete
    )
}