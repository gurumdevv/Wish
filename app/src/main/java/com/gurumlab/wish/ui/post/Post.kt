package com.gurumlab.wish.ui.post

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.Gray01
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.theme.defaultPlaceHolderColor
import com.gurumlab.wish.ui.util.CustomTextField
import com.gurumlab.wish.ui.util.CustomWideButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PostTitle(titleTextRsc: Int) {
    Text(
        text = stringResource(id = titleTextRsc),
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PostSubTitle(textRsc: Int) {
    Text(
        text = stringResource(id = textRsc),
        fontSize = 20.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PostTextField(
    text: String,
    placeHolderRsc: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    imeOption: ImeAction = ImeAction.Default
) {
    CustomTextField(
        text = text,
        placeholderText = stringResource(id = placeHolderRsc),
        fontSize = 16,
        placeholderTextSize = 14,
        imeOption = imeOption,
        singleLine = singleLine,
        minLines = minLines,
        onValueChange = onValueChange,
        modifier = modifier
    )
}

@Composable
fun PostTitleDescription(descriptionTextRsc: Int) {
    Text(
        text = stringResource(id = descriptionTextRsc),
        fontSize = 16.sp,
        color = Color.White,
    )
}

@Composable
fun PostDescription(descriptionText: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(
                defaultBoxColor
            )
            .padding(16.dp),
        text = descriptionText,
        fontSize = 16.sp,
        color = Color.White
    )
}

@Composable
fun LimitTextLength(textLength: Int, maxLength: Int) {
    Row {
        Text(
            text = textLength.toString(),
            color = if (textLength == 0) Gray01 else if (textLength < maxLength) Color.White else Color.Red,
            fontSize = 14.sp
        )
        Text(
            text = "/$maxLength",
            color = Gray01,
            fontSize = 14.sp
        )
    }
}

@Composable
fun PostMultiLineTextField(
    text: String,
    placeHolderRsc: Int,
    textSize: Int,
    placeHolderTextSize: Int,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    imeOption: ImeAction = ImeAction.Default
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(id = placeHolderRsc),
                fontSize = placeHolderTextSize.sp
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedPlaceholderColor = defaultPlaceHolderColor,
            unfocusedPlaceholderColor = defaultPlaceHolderColor,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            selectionColors = TextSelectionColors(
                handleColor = Color.White,
                backgroundColor = Color.White.copy(alpha = 0.4f)
            )
        ),
        textStyle = TextStyle(fontSize = textSize.sp),
        keyboardOptions = KeyboardOptions(
            imeAction = imeOption
        )
    )
}

// <-- PostStart -->
@Composable
fun PostStartTitleSection() {
    PostTitle(titleTextRsc = R.string.post_start_title)
    Spacer(modifier = Modifier.height(16.dp))
    PostTitleDescription(descriptionTextRsc = R.string.post_title_description)
}

@Composable
fun PostProjectTitleSection(
    text: String,
    currentTextLength: Int,
    maxTextLength: Int,
    onValueChange: (String) -> Unit
) {
    PostSubTitle(textRsc = R.string.post_project_title)
    Spacer(modifier = Modifier.height(8.dp))
    PostTextField(
        text = text,
        placeHolderRsc = R.string.post_project_title_placeholder,
        onValueChange = onValueChange,
        imeOption = ImeAction.Next
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        LimitTextLength(
            textLength = currentTextLength,
            maxLength = maxTextLength,
        )
    }
}

@Composable
fun PostOneLineDescriptionSection(
    text: String,
    currentTextLength: Int,
    maxTextLength: Int,
    onValueChange: (String) -> Unit
) {
    PostSubTitle(textRsc = R.string.post_one_line_description)
    Spacer(modifier = Modifier.height(8.dp))
    PostTextField(
        text = text,
        placeHolderRsc = R.string.post_one_line_description_placeholder,
        onValueChange = onValueChange,
        imeOption = ImeAction.Next
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        LimitTextLength(
            textLength = currentTextLength,
            maxLength = maxTextLength,
        )
    }
}

@Composable
fun PostSimpleDescriptionSection(
    text: String,
    currentTextLength: Int,
    maxTextLength: Int,
    onValueChange: (String) -> Unit
) {
    PostSubTitle(textRsc = R.string.post_simple_description)
    Spacer(modifier = Modifier.height(8.dp))
    PostTextField(
        text = text,
        placeHolderRsc = R.string.post_simple_description_placeholder,
        onValueChange = onValueChange,
        singleLine = false,
        imeOption = ImeAction.Done
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        LimitTextLength(
            textLength = currentTextLength,
            maxLength = maxTextLength,
        )
    }
}

@Composable
fun PostStartButtonSection(
    onClick: () -> Unit
) {
    CustomWideButton(
        text = stringResource(R.string.next),
        onClick = onClick
    )
}

// <-- PostFeatures -->
@Composable
fun PostFeaturesLazyColumn(
    itemCount: Int,
    featureTitles: Map<Int, String>,
    featureDescriptions: Map<Int, String>,
    selectedImageUris: Map<Int, List<Uri>>,
    listState: LazyListState,
    scope: CoroutineScope,
    onTitleChange: (Int, String) -> Unit,
    onDescriptionChange: (Int, String) -> Unit,
    onAddButtonClick: (Int) -> Unit,
    onSelectedItemIndexChange: (Int) -> Unit,
    onCameraButtonClick: () -> Unit
) {
    LazyColumn(
        state = listState
    ) {
        items(itemCount) { index ->
            PostFeaturesItem(
                index = index,
                titleTextRsc = R.string.features_item_title,
                titleFieldText = featureTitles.getOrElse(index) { "" },
                descriptionFieldText = featureDescriptions.getOrElse(index) { "" },
                imageUris = selectedImageUris.getOrElse(index) { emptyList() },
                onIndexChange = { onSelectedItemIndexChange(it) },
                onTitleChange = { onTitleChange(index, it) },
                onDescriptionChange = { onDescriptionChange(index, it) },
                onAddButtonClick = {
                    onAddButtonClick(index)
                    moveToLastItem(itemIndex = index + 1, listState = listState, scope = scope)
                },
                onCameraButtonClick = onCameraButtonClick
            )
        }
    }
}


fun moveToLastItem(
    itemIndex: Int,
    listState: LazyListState,
    scope: CoroutineScope
) {
    scope.launch {
        listState.animateScrollToItem(itemIndex)
    }
}

@Composable
fun PostFeaturesTitleWithButton(
    titleTextRsc: Int,
    btnTextRsc: Int,
    onClick: () -> Unit,
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
            onClick = onClick
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
    index: Int,
    titleTextRsc: Int,
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
        selectedIndex = index,
        imageUris = imageUris,
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
        placeholderText = stringResource(id = R.string.features_item_title_placeholder),
        fontSize = 16,
        placeholderTextSize = 16,
        onValueChange = onTitleChange
    )
    Spacer(modifier = Modifier.height(8.dp))
    PostMultiLineTextField(
        text = description,
        placeHolderRsc = R.string.features_item_description_placeholder,
        textSize = 16,
        placeHolderTextSize = 16,
        onValueChange = onDescriptionChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun PostFeaturesItemPhotos(
    selectedIndex: Int,
    imageUris: List<Uri>,
    onIndexChange: (Int) -> Unit,
    onButtonClick: () -> Unit
) {
    LazyRow {
        item {
            Button(
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
                modifier = Modifier
                    .size(65.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = stringResource(R.string.btn_gallery)
                    )
                    Text(
                        text = stringResource(R.string.btn_gallery),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        items(imageUris.size) { index ->
            AsyncImage(
                model = imageUris[index],
                contentDescription = stringResource(R.string.features_item_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

// <-- PostExamination -->
@Composable
fun PostDescriptionTitleSection() {
    PostTitle(titleTextRsc = R.string.post_description_title)
    Spacer(modifier = Modifier.height(16.dp))
    PostTitleDescription(descriptionTextRsc = R.string.post_title_description)
}

@Composable
fun PostDescriptionTextFieldSection(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        PostSubTitle(textRsc = R.string.post_project_description)
        Spacer(modifier = Modifier.height(8.dp))
        PostMultiLineTextField(
            text = text,
            placeHolderRsc = R.string.post_project_description_placeholder,
            textSize = 16,
            placeHolderTextSize = 16,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(defaultBoxColor)
                .verticalScroll(rememberScrollState()),
            imeOption = ImeAction.Done
        )
    }
}

@Composable
fun PostDescriptionButtonSection(
    onClick: () -> Unit
) {
    CustomWideButton(
        text = stringResource(R.string.next),
        onClick = onClick
    )
}

// <-- PostExamination -->
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

@Composable
fun PostExaminationButtonSection(
    onClick: () -> Unit
) {
    CustomWideButton(
        text = stringResource(R.string.submit),
        onClick = onClick
    )
}