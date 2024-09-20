package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.Wish

@Composable
fun ProjectSubmitRoute(wish: Wish, wishId: String, onComplete: () -> Unit) {
    val viewModel = hiltViewModel<ProjectSubmitViewModel>()
    ProjectSubmitScreen(viewModel, wish, wishId, onComplete)
}