package com.gurumlab.wish.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun PostDescriptionScreen(
    viewModel: PostViewModel,
    onPostFeatures: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PostDescriptionContent(
            viewModel = viewModel,
            onPostFeatures = onPostFeatures,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun PostDescriptionContent(
    viewModel: PostViewModel,
    onPostFeatures: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val buttonAndAroundPaddingHeight = 110.dp

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PostDescriptionTitleSection()
            Spacer(modifier = Modifier.height(16.dp))

            PostDescriptionTextFieldSection(
                text = viewModel.projectDescription,
                onValueChange = { viewModel.updateProjectDescription(it) },
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(PaddingValues(bottom = buttonAndAroundPaddingHeight))
                    .imePadding()
            )
            Spacer(modifier = Modifier.height(24.dp))

            PostDescriptionButtonSection(
                projectDescription = viewModel.projectDescription,
                onPostFeatures = onPostFeatures,
                onSnackbarMessageChange = { viewModel.updateSnackbarMessage(it) }
            )
            Spacer(modifier = Modifier.size(24.dp))
        }

        PostScreensSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        )
    }

    LaunchedEffect(viewModel.snackbarMessageRes) {
        showSnackbar(
            snackbarMessageRes = viewModel.snackbarMessageRes,
            context = context,
            snackbarHostState = snackbarHostState,
            resetSnackbarMessage = { viewModel.resetSnackbarMessage() }
        )
    }
}