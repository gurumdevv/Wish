package com.gurumlab.wish.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomLoadingScreen
import com.gurumlab.wish.ui.util.ErrorSnackBar
import com.gurumlab.wish.ui.util.showSnackbar

@Composable
fun PostExaminationScreen(
    viewModel: PostViewModel,
    onWish: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PostExaminationContent(
            viewModel = viewModel,
            onWish = onWish,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun PostExaminationContent(
    viewModel: PostViewModel,
    onWish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uploadState by viewModel.postExaminationUiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PostTitle(titleTextRsc = R.string.post_examination_title)
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_project_title,
                        description = viewModel.projectTitle
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_one_line_description,
                        description = viewModel.oneLineDescription
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_simple_description,
                        description = viewModel.simpleDescription
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_project_description,
                        description = viewModel.projectDescription.text
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostTitle(titleTextRsc = R.string.features_item_title)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(count = viewModel.featureTitles.size) { index ->
                    PostExaminationFeaturesItem(
                        title = viewModel.featureTitles[index] ?: "",
                        description = viewModel.featureDescriptions[index]?.text ?: "",
                        imageUris = viewModel.selectedImageUris[index] ?: emptyList()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    Spacer(modifier = Modifier.size(24.dp))
                    PostExaminationButtonSection { viewModel.uploadPost() }
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        }

        if (viewModel.isLoading) {
            CustomLoadingScreen()
        }

        ErrorSnackBar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        )
    }

    LaunchedEffect(uploadState) {
        when (uploadState) {
            PostExaminationUiState.Success -> {
                onWish()
            }

            PostExaminationUiState.Failed -> {
                viewModel.showSnackbarMessage(R.string.upload_fail)
            }

            else -> {}
        }
    }

    LaunchedEffect(viewModel.snackbarMessageRes.value) {
        showSnackbar(
            snackbarMessageRes = viewModel.snackbarMessageRes.value,
            context = context,
            snackbarHostState = snackbarHostState,
        )
    }
}