package com.gurumlab.wish.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent

@Composable
fun DetailScreen(
    wishId: String,
    viewModel: DetailViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.loadWish(wishId)
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        DetailContent(
            wishId = wishId,
            viewModel = viewModel,
            onProgressScreen = onProgressScreen,
            onMessageScreen = onMessageScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        )
    }
}

@Composable
fun DetailContent(
    wishId: String,
    viewModel: DetailViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    onMessageScreen: (MinimizedWish) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val startingWishUiState by viewModel.startingWishUiState.collectAsStateWithLifecycle()

    when (uiState) {
        DetailUiState.Loading -> {
            DetailLoadingScreen(modifier)
        }

        DetailUiState.Fail -> {
            DetailFailScreen(wishId = wishId, viewModel = viewModel)
        }

        is DetailUiState.Success -> {
            val wish = (uiState as DetailUiState.Success).wish
            val currentDate = remember { viewModel.fetchCurrentDate() }
            val currentUser = remember { viewModel.fetchUserInfo() }
            val minimizedWish =
                remember {
                    viewModel.fetchMinimizedWish(
                        wish = wish,
                        currentDate = currentDate,
                        currentUser = currentUser
                    )
                }

            Box(
                modifier = modifier
            ) {
                ProjectDescriptionArea(scrollState = scrollState, wish = wish)
                DetailScreenButtonArea(
                    minimizedWish = minimizedWish,
                    onUpdateWish = { viewModel.updateWish(wishId, currentDate, currentUser) },
                    onMessageScreen = onMessageScreen,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp)
                ) { data ->
                    CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
                }
            }

            LaunchedEffect(
                startingWishUiState.isStatusUpdateSuccess,
                startingWishUiState.isStartedDateUpdateSuccess,
                startingWishUiState.isDeveloperIdUpdateSuccess,
                startingWishUiState.isDeveloperNameUpdateSuccess
            ) {
                if (startingWishUiState.isStatusUpdateSuccess
                    && startingWishUiState.isStartedDateUpdateSuccess
                    && startingWishUiState.isDeveloperIdUpdateSuccess
                    && startingWishUiState.isDeveloperNameUpdateSuccess
                ) {
                    onProgressScreen(minimizedWish, wishId)
                }
            }
        }
    }

    LaunchedEffect(startingWishUiState.isFailUpdateWish) {
        if (startingWishUiState.isFailUpdateWish) {
            snackbarHostState.showSnackbar(
                message = context.getString(R.string.please_try_again_later),
                duration = SnackbarDuration.Short
            )
        }
    }
}