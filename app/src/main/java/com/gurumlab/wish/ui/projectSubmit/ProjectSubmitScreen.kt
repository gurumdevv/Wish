package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun ProjectSubmitScreen(
    wishId: String,
    minimizedWish: MinimizedWish,
    viewModel: ProjectSubmitViewModel,
    onComplete: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        ProjectSubmitContent(
            wishId = wishId,
            minimizedWish = minimizedWish,
            viewModel = viewModel,
            onComplete = onComplete,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun ProjectSubmitContent(
    wishId: String,
    minimizedWish: MinimizedWish,
    viewModel: ProjectSubmitViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading
    val projectUpdateUiState by viewModel.projectUpdateUiState.collectAsStateWithLifecycle()
    val projectSubmitInputFieldUiState by viewModel.projectSubmitInputFieldUiState

    Box(
        modifier = modifier
    ) {
        ProjectSubmitInputArea(
            minimizedWish = minimizedWish,
            projectSubmitInputFieldUiState = projectSubmitInputFieldUiState,
            scrollState = scrollState,
            onRepositoryInfoChange = {
                viewModel.onProjectSubmitInputFieldChange(repositoryInfo = it)
            },
            onAccountInfoChange = {
                viewModel.onProjectSubmitInputFieldChange(accountInfo = it)
            },
            onAccountOwnerChange = {
                viewModel.onProjectSubmitInputFieldChange(accountOwner = it)
            }
        )

        ProjectSubmitButtonArea(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            viewModel.submitWish(
                wishId = wishId,
                minimizedWish = minimizedWish,
                emptySnackbarMessageRes = R.string.blank,
                failSnackbarMessageRes = R.string.fail_submit,
                projectCompletedDescription = context.getString(R.string.project_completed)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        ) { data ->
            CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
        }

        if (isLoading) {
            ProjectSubmitLoadingScreen(
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.Center)
            )
        }
    }

    LaunchedEffect(
        projectUpdateUiState.isSubmitSuccess,
        projectUpdateUiState.isStatusUpdateSuccess,
        projectUpdateUiState.isCompletedDateUpdateSuccess
    ) {
        if (projectUpdateUiState.isSubmitSuccess
            && projectUpdateUiState.isStatusUpdateSuccess
            && projectUpdateUiState.isCompletedDateUpdateSuccess
        ) {
            onComplete()
        }
    }

    LaunchedEffect(viewModel.snackbarMessage.value) {
        viewModel.snackbarMessage.value?.let {
            snackbarHostState.showSnackbar(
                message = context.getString(it),
                duration = SnackbarDuration.Short
            )
        }
    }
}