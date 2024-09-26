package com.gurumlab.wish.ui.post

import androidx.compose.runtime.Composable

@Composable
fun PostStartRoute(viewModel: PostViewModel, onPostDescription: () -> Unit) {
    PostStartScreen(viewModel, onPostDescription)
}

@Composable
fun PostDescriptionRoute(viewModel: PostViewModel, onPostFeatures: () -> Unit) {
    PostDescriptionScreen(viewModel, onPostFeatures)
}

@Composable
fun PostFeaturesRoute(viewModel: PostViewModel, onPostExamination: () -> Unit) {
    PostFeaturesScreen(viewModel, onPostExamination)
}

@Composable
fun PostExaminationRoute(viewModel: PostViewModel, onWish: () -> Unit) {
    PostExaminationScreen(viewModel, onWish)
}