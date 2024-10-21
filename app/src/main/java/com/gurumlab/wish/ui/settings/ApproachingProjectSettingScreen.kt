package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun ApproachingProjectSettingScreen(
    viewModel: SettingsViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        ApproachingProjectSettingContent(
            viewModel = viewModel,
            onProgressScreen = onProgressScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun ApproachingProjectSettingContent(
    viewModel: SettingsViewModel,
    onProgressScreen: (MinimizedWish, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.approachingProjectUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState == ApproachingProjectUiState.Loading) {
            viewModel.loadApproachingWishes()
        }
    }

    when (uiState) {
        ApproachingProjectUiState.Loading -> {
            ApproachingProjectLoadingScreen(modifier)
        }

        ApproachingProjectUiState.Empty -> {
            ApproachingProjectWishesList(
                totalCount = 0,
                successCount = 0,
                wishes = emptyMap(),
                getMinimizedWish = null,
                onProgressScreen = null,
                modifier = modifier
            )
        }

        ApproachingProjectUiState.Exception -> {
            ProjectExceptionScreen(modifier)
        }

        is ApproachingProjectUiState.Success -> {
            val wishes = (uiState as ApproachingProjectUiState.Success).wishes
            val totalCount = wishes.keys.size
            val successCount = wishes.values.count { it.status == WishStatus.COMPLETED.ordinal }

            ApproachingProjectWishesList(
                wishes = wishes,
                totalCount = totalCount,
                successCount = successCount,
                getMinimizedWish = viewModel::getMinimizedWish,
                onProgressScreen = onProgressScreen,
                modifier = modifier
            )
        }
    }
}