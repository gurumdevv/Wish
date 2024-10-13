package com.gurumlab.wish.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun PostStartScreen(
    viewModel: PostViewModel,
    onPostDescription: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PostStartContent(
            viewModel = viewModel,
            onPostDescription = onPostDescription,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PostStartContent(
    viewModel: PostViewModel,
    onPostDescription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            PostStartTitleSection()
            Spacer(modifier = Modifier.height(16.dp))

            PostProjectTitleSection(
                text = viewModel.projectTitle,
                currentTextLength = viewModel.projectTitle.length,
                maxTextLength = 15
            ) {
                viewModel.updateProjectTitle(if (it.length <= 15) it else it.substring(0, 15))
            }
            Spacer(modifier = Modifier.height(8.dp))

            PostOneLineDescriptionSection(
                text = viewModel.oneLineDescription,
                currentTextLength = viewModel.oneLineDescription.length,
                maxTextLength = 15
            ) {
                viewModel.updateOneLineDescription(if (it.length <= 15) it else it.substring(0, 15))
            }
            Spacer(modifier = Modifier.height(8.dp))

            PostSimpleDescriptionSection(
                text = viewModel.simpleDescription,
                currentTextLength = viewModel.simpleDescription.length,
                maxTextLength = 30
            ) {
                viewModel.updateSimpleDescription(if (it.length <= 30) it else it.substring(0, 30))
            }
            Spacer(modifier = Modifier.weight(1f))

            PostStartButtonSection(
                projectTitle = viewModel.projectTitle,
                oneLineDescription = viewModel.oneLineDescription,
                simpleDescription = viewModel.simpleDescription,
                onClick = onPostDescription,
                onError = {
                    viewModel.updateSnackbarMessage(R.string.blank)
                }
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