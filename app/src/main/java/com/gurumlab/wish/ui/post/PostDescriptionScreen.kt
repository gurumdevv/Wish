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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomWideButton
import kotlinx.coroutines.launch

@Composable
fun PostDescriptionScreen(viewModel: PostViewModel, onPostFeatures: () -> Unit) {
    PostDescriptionContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel,
        onPostFeatures = onPostFeatures
    )
}

@Composable
fun PostDescriptionContent(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel,
    onPostFeatures: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val projectDescription = viewModel.projectDescription.value
    val buttonAndAroundPaddingHeight = 110.dp

    Box(
        modifier = modifier
    ) {
        Column {
            PostDescriptionTitleSection()
            Spacer(modifier = Modifier.height(16.dp))
            PostDescriptionTextFieldSection(
                modifier = Modifier
                    .weight(1f)
                    .consumeWindowInsets(PaddingValues(bottom = buttonAndAroundPaddingHeight))
                    .imePadding(),
                text = projectDescription,
                onValueChange = { viewModel.setProjectDescription(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomWideButton(
                text = stringResource(R.string.next)
            ) {
                if (projectDescription.isNotBlank()) {
                    onPostFeatures()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.blank),
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(24.dp))
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

@Composable
fun PostDescriptionTitleSection() {
    PostTitle(titleTextRsc = R.string.post_description_title)
    Spacer(modifier = Modifier.height(16.dp))
    PostTitleDescription(descriptionTextRsc = R.string.post_title_description)
}

@Composable
fun PostDescriptionTextFieldSection(
    modifier: Modifier,
    text: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        PostSubTitle(textRsc = R.string.post_project_description)
        Spacer(modifier = Modifier.height(8.dp))
        PostMultiLineTextField(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(defaultBoxColor)
                .verticalScroll(rememberScrollState()),
            text = text,
            onValueChange = onValueChange,
            placeHolderRsc = R.string.post_project_description_placeholder,
            textSize = 16,
            placeHolderTextSize = 16,
            imeOption = ImeAction.Done
        )
    }
}