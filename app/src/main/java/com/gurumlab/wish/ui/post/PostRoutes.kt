package com.gurumlab.wish.ui.post

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostStartRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onPostDescription: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    PostStartScreen(
        topBar = topBar,
        viewModel = viewModel,
        onPostDescription = onPostDescription
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDescriptionRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onPostFeatures: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    PostDescriptionScreen(
        topBar = topBar,
        viewModel = viewModel,
        onPostFeatures = onPostFeatures
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFeaturesRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onPostExamination: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    PostFeaturesScreen(
        topBar = topBar,
        viewModel = viewModel,
        onPostExamination = onPostExamination
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostExaminationRoute(
    viewModel: PostViewModel,
    onNavUp: () -> Unit,
    onWish: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    PostExaminationScreen(
        topBar = topBar,
        viewModel = viewModel,
        onWish = onWish
    )
}