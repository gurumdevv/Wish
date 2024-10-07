package com.gurumlab.wish.ui.post

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomLoadingScreen
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomWideButton
import kotlinx.coroutines.launch

@Composable
fun PostExaminationScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    viewModel: PostViewModel,
    onWish: () -> Unit
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PostExaminationContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp),
            viewModel = viewModel,
            onWish = onWish
        )
    }
}

@Composable
fun PostExaminationContent(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel,
    onWish: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = modifier
    ) {
        Column {
            PostTitle(titleTextRsc = R.string.post_examination_title)
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_project_title,
                        description = viewModel.projectTitle.value
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_one_line_description,
                        description = viewModel.oneLineDescription.value
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_simple_description,
                        description = viewModel.simpleDescription.value
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostExaminationDefaultItem(
                        titleRsc = R.string.post_project_description,
                        description = viewModel.projectDescription.value
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PostTitle(titleTextRsc = R.string.features_item_title)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(viewModel.itemCount.value) { index ->
                    PostExaminationFeaturesItem(
                        title = viewModel.featureTitles[index] ?: "",
                        description = viewModel.featureDescriptions[index] ?: "",
                        imageUris = viewModel.selectedImageUris[index] ?: emptyList()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Column {
                Spacer(modifier = Modifier.height(24.dp))
                CustomWideButton(
                    text = stringResource(R.string.submit)
                ) {
                    viewModel.uploadPost()
                }
                Spacer(modifier = Modifier.size(24.dp))
            }
        }

        if (viewModel.isLoading.value) {
            CustomLoadingScreen()
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        ) { data ->
            CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
        }

        LaunchedEffect(viewModel.isPostUploaded.intValue) {
            when (viewModel.isPostUploaded.intValue) {
                UploadState.SUCCESS.ordinal -> {
                    onWish()
                }

                UploadState.FAILED.ordinal -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.upload_fail),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostExaminationDefaultItem(
    titleRsc: Int,
    description: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        PostSubTitle(textRsc = titleRsc)
        Spacer(modifier = Modifier.height(8.dp))
        PostDescription(descriptionText = description)
    }
}

@Composable
fun PostExaminationFeaturesItem(
    title: String,
    description: String,
    imageUris: List<Uri>
) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color.White,
    )
    Spacer(modifier = Modifier.height(8.dp))
    PostDescription(descriptionText = description)
    Spacer(modifier = Modifier.height(8.dp))
    imageUris.forEach { uri ->
        AsyncImage(
            model = uri,
            contentDescription = stringResource(R.string.features_item_image),
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}