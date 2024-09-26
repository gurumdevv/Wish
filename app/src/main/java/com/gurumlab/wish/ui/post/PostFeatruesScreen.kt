package com.gurumlab.wish.ui.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
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
        onFinishClick = {} //TODO("something to do with viewModel")
    )
}

@Composable
fun PostFeaturesContent(
    modifier: Modifier = Modifier,
    onFinishClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var itemCount by remember { mutableIntStateOf(1) }
    val titles = remember { mutableStateMapOf<Int, String>() }
    val descriptions = remember { mutableStateMapOf<Int, String>() }

    Column(
        modifier = modifier
    ) {
        PostTitleWithButton(
            titleTextRsc = R.string.post_features_title,
            btnTextRsc = R.string.btn_finish,
            onClick = onFinishClick
        )

        LazyColumn(
            state = listState
        ) {
            items(itemCount) { index ->
                PostFeaturesItem(
                    titleTextRsc = R.string.features_item_title,
                    count = index + 1,
                    titleFieldText = titles.getOrElse(index) { "" },
                    descriptionFieldText = descriptions.getOrElse(index) { "" },
                    onTitleChange = { titles[index] = it },
                    onDescriptionChange = { descriptions[index] = it },
                    onAddButtonClick = {
                        itemCount++
                        moveToLastItem(
                            scope = scope,
                            listState = listState,
                            itemCount = itemCount - 1
                        )
                    }
                )
            }
        }
    }
}

fun moveToLastItem(
    scope: CoroutineScope,
    listState: LazyListState,
    itemCount: Int
) {
    scope.launch {
        listState.animateScrollToItem(itemCount)
    }
}

@Composable
fun PostTitleWithButton(
    titleTextRsc: Int,
    btnTextRsc: Int,
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
            onClick = { onClick() }) {
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
    count: Int,
    titleFieldText: String,
    descriptionFieldText: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddButtonClick: () -> Unit
) {
    PostFeaturesItemTitle(textRsc = titleTextRsc, count = count)
    Spacer(modifier = Modifier.height(8.dp))
    PostFeaturesItemDescription(
        title = titleFieldText,
        description = descriptionFieldText,
        onTitleChange = onTitleChange,
        onDescriptionChange = onDescriptionChange
    )
    Spacer(modifier = Modifier.height(8.dp))
    PostFeaturesItemPhotos()
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
fun PostFeaturesItemPhotos() {
    LazyRow {
        item {
            Column(
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        defaultBoxColor
                    )
                    .padding(5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = stringResource(
                        R.string.btn_gallery
                    )
                )
                Text(
                    text = stringResource(id = R.string.btn_gallery),
                    fontSize = 12.sp,
                    color = Color.White,
                )
            }
        }
    }
}