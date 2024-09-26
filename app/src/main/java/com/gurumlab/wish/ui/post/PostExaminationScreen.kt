package com.gurumlab.wish.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomWideButton

@Composable
fun PostExaminationScreen(viewModel: PostViewModel, onWish: () -> Unit) {
    PostExaminationContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel,
        onWish = onWish
    )
}

@Composable
fun PostExaminationContent(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel,
    onWish: () -> Unit
) {
    Column(modifier = modifier) {
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
            items(viewModel.itemCount.intValue) { index ->
                PostExaminationFeaturesItem(
                    title = viewModel.featureTitles[index] ?: "",
                    description = viewModel.featureDescriptions[index] ?: "",
                    images = emptyList() //TODO("Load images")
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Column {
            Spacer(modifier = Modifier.height(24.dp))
            CustomWideButton(
                text = stringResource(R.string.submit)
            ) {
                //TODO("Submit in viewmodel")
                onWish() //TODO("if submit is success")
            }
            Spacer(modifier = Modifier.size(24.dp))
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
    images: List<Int>
) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = Color.White,
    )
    Spacer(modifier = Modifier.height(8.dp))
    PostDescription(descriptionText = description)
    Spacer(modifier = Modifier.height(8.dp))
    //TODO("Load images")
}