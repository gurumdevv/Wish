package com.gurumlab.wish.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomExceptionScreen
import com.gurumlab.wish.ui.util.CustomLoadingScreen

@Composable
fun DetailScreen(
    wishId: String,
    viewModel: DetailViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {

    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        DetailContent(
            wishId = wishId,
            viewModel = viewModel,
            onProgressScreen = onProgressScreen,
            onMessageScreen = onMessageScreen
        )
    }
}

@Composable
fun DetailContent(
    wishId: String,
    viewModel: DetailViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val scrollState = rememberScrollState()
    val wish = viewModel.wish.collectAsStateWithLifecycle(initialValue = null)
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isFailed = viewModel.isFailed.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.retryCount) {
        viewModel.loadWish(wishId)
    }

    if (isLoading.value) {
        CustomLoadingScreen(
            modifier = Modifier.background(Color.Black)
        )
    } else {
        if (isFailed.value) {
            CustomExceptionScreen(
                titleRsc = R.string.cannot_load_data,
                descriptionRsc = R.string.retry_load_wish,
                onClick = { viewModel.retryLoadWish(wishId) }
            )
        } else {
            wish.value?.let { loadedWish ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ProjectDescriptionArea(scrollState = scrollState, wish = loadedWish)
                    DetailScreenButtonArea(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        wish = loadedWish,
                        wishId = wishId,
                        onProgressScreen = onProgressScreen,
                        onMessageScreen = onMessageScreen
                    )
                }
            }
        }
    }
}