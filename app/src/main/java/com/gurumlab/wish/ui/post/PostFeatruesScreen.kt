package com.gurumlab.wish.ui.post

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomTextField
import com.gurumlab.wish.ui.util.CustomWideButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PostFeaturesScreen(viewModel: PostViewModel, onPostExamination: () -> Unit) {
    PostFeaturesContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel,
        onFinishClick = onPostExamination
    )
}

@Composable
fun PostFeaturesContent(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel,
    onFinishClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val itemCount = viewModel.itemCount.intValue
    val titles = viewModel.featureTitles
    val descriptions = viewModel.featureDescriptions
    val imageUris = viewModel.selectedImageUris

    var selectedItemIndex by remember { mutableIntStateOf(-1) }
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (selectedItemIndex != -1) viewModel.setSelectedImageUris(selectedItemIndex, uris)
        }
    )

    Box(
        modifier = modifier
    ) {
        Column {
            PostTitleWithButton(
                titleTextRsc = R.string.post_features_title,
                btnTextRsc = R.string.btn_finish,
                viewModel = viewModel,
                context = context,
                scope = scope,
                snackbarHostState = snackbarHostState,
                onClick = onFinishClick
            )

            LazyColumn(
                state = listState
            ) {
                items(itemCount) { index ->
                    PostFeaturesItem(
                        titleTextRsc = R.string.features_item_title,
                        index = index,
                        titleFieldText = titles.getOrElse(index) { "" },
                        descriptionFieldText = descriptions.getOrElse(index) { "" },
                        imageUris = imageUris.getOrElse(index) { emptyList() },
                        onIndexChange = { selectedItemIndex = it },
                        onTitleChange = { viewModel.setFeatureTitles(index, it) },
                        onDescriptionChange = { viewModel.setFeatureDescriptions(index, it) },
                        onAddButtonClick = {
                            viewModel.setItemCount(index + 2)
                            viewModel.setFeatureTitles(index + 1, "")
                            viewModel.setFeatureDescriptions(index + 1, "")
                            moveToLastItem(
                                scope = scope,
                                listState = listState,
                                itemIndex = index + 1
                            )
                        },
                        onCameraButtonClick = {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        ) { data ->
            CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
        }
    }
}

fun moveToLastItem(
    scope: CoroutineScope,
    listState: LazyListState,
    itemIndex: Int
) {
    scope.launch {
        listState.animateScrollToItem(itemIndex)
    }
}

@Composable
fun PostTitleWithButton(
    titleTextRsc: Int,
    btnTextRsc: Int,
    viewModel: PostViewModel,
    context: Context,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = titleTextRsc),
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = defaultBoxColor
            ),
            onClick = {
                if (viewModel.featureTitles.values.any { it.isBlank() } || viewModel.featureDescriptions.values.any { it.isBlank() }) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.blank),
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {
                    onClick()
                }
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = btnTextRsc),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PostFeaturesItem(
    titleTextRsc: Int,
    index: Int,
    titleFieldText: String,
    descriptionFieldText: String,
    imageUris: List<Uri>,
    onIndexChange: (Int) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddButtonClick: () -> Unit,
    onCameraButtonClick: () -> Unit
) {
    PostFeaturesItemTitle(textRsc = titleTextRsc, count = index + 1)
    Spacer(modifier = Modifier.height(8.dp))
    PostFeaturesItemDescription(
        title = titleFieldText,
        description = descriptionFieldText,
        onTitleChange = onTitleChange,
        onDescriptionChange = onDescriptionChange
    )
    Spacer(modifier = Modifier.height(8.dp))
    PostFeaturesItemPhotos(
        imageUris = imageUris,
        selectedIndex = index,
        onIndexChange = onIndexChange,
        onButtonClick = onCameraButtonClick
    )
    Spacer(modifier = Modifier.height(16.dp))
    CustomWideButton(
        text = stringResource(R.string.add_features_item),
        onClick = onAddButtonClick
    )
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
fun PostFeaturesItemTitle(
    textRsc: Int,
    count: Int,
) {
    Text(
        text = "${stringResource(id = textRsc)} $count",
        fontSize = 20.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PostFeaturesItemDescription(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    CustomTextField(
        text = title,
        onValueChange = onTitleChange,
        placeholderText = stringResource(id = R.string.features_item_title_placeholder),
        fontSize = 16,
        placeholderTextSize = 16
    )
    Spacer(modifier = Modifier.height(8.dp))
    PostMultiLineTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .verticalScroll(rememberScrollState()),
        text = description,
        onValueChange = onDescriptionChange,
        placeHolderRsc = R.string.features_item_description_placeholder,
        textSize = 16,
        placeHolderTextSize = 16
    )
}

@Composable
fun PostFeaturesItemPhotos(
    imageUris: List<Uri>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    onButtonClick: () -> Unit
) {
    LazyRow {
        item {
            Button(
                modifier = Modifier
                    .size(65.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = defaultBoxColor
                ),
                contentPadding = PaddingValues(4.dp),
                onClick = {
                    onIndexChange(selectedIndex)
                    onButtonClick()
                },
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = stringResource(
                            R.string.btn_gallery
                        )
                    )
                    Text(
                        text = stringResource(
                            R.string.btn_gallery
                        ),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        items(imageUris.size) {
            AsyncImage(
                model = imageUris[it],
                contentDescription = stringResource(R.string.features_item_image),
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}