package com.gurumlab.wish.ui.home

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.ErrorSnackBar
import com.gurumlab.wish.ui.util.showSnackbar

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onDetailScreen: (wishId: String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    PermissionCheck(viewModel = viewModel)

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        HomeContent(
            viewModel = viewModel,
            onDetailScreen = onDetailScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
        )
    }
}

@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    onDetailScreen: (wishId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        when (uiState) {
            HomeUiState.Loading -> {
                HomeLoadingScreen()
            }

            HomeUiState.Empty -> {
                HomeErrorScreen()
            }

            HomeUiState.Exception -> {
                HomeExceptionScreen {
                    viewModel.loadWishes()
                }
            }

            is HomeUiState.Success -> {
                val wishes = (uiState as HomeUiState.Success).wishes
                val pagerState = rememberPagerState(pageCount = { wishes.keys.size })

                HomeVerticalPager(
                    wishes = wishes,
                    pagerState = pagerState,
                    onLikeClick = { viewModel.updateLikeCount(it) },
                    onDetailScreen = onDetailScreen,
                )
            }
        }

        ErrorSnackBar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
        )
    }

    LaunchedEffect(viewModel.snackbarMessageRes.value) {
        showSnackbar(
            snackbarMessageRes = viewModel.snackbarMessageRes.value,
            context = context,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun PermissionCheck(
    viewModel: HomeViewModel
) {
    var isDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.wasPermissionChecked.value) {
        if (viewModel.wasPermissionChecked.value == false) {
            isDialogOpen = true
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && isDialogOpen) {
        FeatureThatRequiresNotificationPermission { isDialogOpen = false }
        viewModel.updatePermissionCheck()
    }
}