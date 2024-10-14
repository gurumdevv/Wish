package com.gurumlab.wish.ui.post

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.ErrorSnackBarMessage
import com.gurumlab.wish.ui.util.showSnackbar

@Composable
fun PostFeaturesScreen(
    viewModel: PostViewModel,
    onPostExamination: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PostFeaturesContent(
            viewModel = viewModel,
            onFinishClick = onPostExamination,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun PostFeaturesContent(
    viewModel: PostViewModel,
    onFinishClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedItemIndex by remember { mutableIntStateOf(-1) }
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (selectedItemIndex != -1) viewModel.updateSelectedImageUris(selectedItemIndex, uris)
        }
    )

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PostFeaturesTitleWithButton(
                titleTextRsc = R.string.post_features_title,
                btnTextRsc = R.string.btn_finish,
                viewModel = viewModel,
                onClick = onFinishClick,
                onSnackbarMessageChange = { viewModel.showSnackbarMessage(it) }
            )

            PostFeaturesLazyColumn(
                itemCount = viewModel.itemCount,
                featureTitles = viewModel.featureTitles,
                featureDescriptions = viewModel.featureDescriptions,
                selectedImageUris = viewModel.selectedImageUris,
                listState = listState,
                scope = scope,
                onTitleChange = { index, text ->
                    viewModel.updateFeatureTitles(index, text)
                },
                onDescriptionChange = { index, text ->
                    viewModel.updateFeatureDescriptions(index, text)
                },
                onAddButtonClick = { index ->
                    viewModel.updateItemCount(index + 2)
                    viewModel.updateFeatureTitles(index + 1, "")
                    viewModel.updateFeatureDescriptions(index + 1, "")
                },
                onSelectedItemIndexChange = { selectedItemIndex = it },
                onCameraButtonClick = {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
        }

        ErrorSnackBarMessage(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        )
    }

    LaunchedEffect(viewModel.snackbarMessageRes.value) {
        showSnackbar(
            snackbarMessageRes = viewModel.snackbarMessageRes.value,
            context = context,
            snackbarHostState = snackbarHostState,
        )
    }
}