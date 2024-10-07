package com.gurumlab.wish.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomExceptionScreen
import com.gurumlab.wish.ui.util.CustomLoadingScreen

@Composable
fun DetailScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    wishId: String,
    viewModel: DetailViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.initializeDetail(wishId)
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        DetailContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding),
            wishId = wishId,
            viewModel = viewModel,
            onProgressScreen = onProgressScreen,
            onMessageScreen = onMessageScreen
        )
    }
}

@Composable
fun DetailContent(
    modifier: Modifier,
    wishId: String,
    viewModel: DetailViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit
) {
    val scrollState = rememberScrollState()
    val wish = viewModel.wish.collectAsStateWithLifecycle(initialValue = null)
    val isStartedDateUpdateSuccess =
        viewModel.isStartedDateUpdateSuccess.collectAsStateWithLifecycle()
    val isDeveloperIdUpdateSuccess =
        viewModel.isDeveloperIdUpdateSuccess.collectAsStateWithLifecycle()
    val isDeveloperNameUpdateSuccess =
        viewModel.isDeveloperNameUpdateSuccess.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isFailed = viewModel.isFailed.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.retryCount.value) {
        if (viewModel.retryCount.value > 0) viewModel.initializeDetail(wishId)
    }

    if (isLoading.value) {
        CustomLoadingScreen(modifier = modifier)
    } else {
        if (isFailed.value) {
            CustomExceptionScreen(
                modifier = modifier,
                titleRsc = R.string.cannot_load_data,
                descriptionRsc = R.string.retry_load_wish,
                onClick = { viewModel.retryLoadWish(wishId) }
            )
        } else {
            wish.value?.let { loadedWish ->
                val minimizedWish = remember {
                    getMinimizedWish(
                        loadedWish,
                        viewModel.currentDate,
                        viewModel.currentUserUid,
                        viewModel.currentUserName
                    )
                }
                val onUpdateWish: () -> Unit = remember {
                    {
                        viewModel.updateStartedDate(wishId, viewModel.currentDate)
                        viewModel.updateDeveloperName(wishId, viewModel.currentUserName)
                        viewModel.updateDeveloperId(wishId, viewModel.currentUserUid)
                    }
                }

                LaunchedEffect(
                    isStartedDateUpdateSuccess.value,
                    isDeveloperIdUpdateSuccess.value,
                    isDeveloperNameUpdateSuccess.value
                ) {
                    if (isStartedDateUpdateSuccess.value && isDeveloperIdUpdateSuccess.value && isDeveloperNameUpdateSuccess.value) {
                        onProgressScreen(minimizedWish, wishId)
                    }
                }

                Box(
                    modifier = modifier
                ) {
                    ProjectDescriptionArea(scrollState = scrollState, wish = loadedWish)
                    DetailScreenButtonArea(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        minimizedWish = minimizedWish,
                        onUpdateWish = onUpdateWish,
                        onMessageScreen = onMessageScreen
                    )
                }
            }
        }
    }
}

fun getMinimizedWish(
    wish: Wish,
    startedDate: Int,
    currentUserUid: String,
    currentUserName: String
): MinimizedWish {
    return MinimizedWish(
        postId = wish.postId,
        createdDate = wish.createdDate,
        startedDate = startedDate,
        completedDate = wish.completedDate,
        posterId = wish.posterId,
        developerId = currentUserUid,
        posterName = wish.posterName,
        developerName = currentUserName,
        title = wish.title,
        simpleDescription = wish.simpleDescription,
        comment = wish.comment
    )
}