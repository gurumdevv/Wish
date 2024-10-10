package com.gurumlab.wish.ui.post

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostStartRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onPostDescription: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    PostStartScreen(
        viewModel = viewModel,
        onPostDescription = onPostDescription,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDescriptionRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onPostFeatures: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    PostDescriptionScreen(
        viewModel = viewModel,
        onPostFeatures = onPostFeatures,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeaturesRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onPostExamination: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    PostFeaturesScreen(
        viewModel = viewModel,
        onPostExamination = onPostExamination,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostExaminationRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onWish: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    PostExaminationScreen(
        viewModel = viewModel,
        onWish = onWish,
        topBar = topBar
    )
}