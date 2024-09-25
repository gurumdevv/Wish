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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomWideButton
import kotlinx.coroutines.launch

@Composable
fun PostDescriptionScreen() {
    PostDescriptionContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp)
    )
}

@Preview
@Composable
fun PreviewPostDescriptionScreen() {
    PostDescriptionScreen()
}

@Composable
fun PostDescriptionContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var projectDescription by remember { mutableStateOf("") }

    Box(
        modifier = modifier
    ) {
        Column {
            PostDescriptionTitleSection()
            Spacer(modifier = Modifier.height(16.dp))
            PostDescriptionTextFieldSection(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                text = projectDescription,
                onValueChange = { projectDescription = it }
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomWideButton(
                text = stringResource(R.string.submit)
            ) {
                if (projectDescription.isNotBlank()) {
                    //TODO("something to do with viewmodel")
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
    PostSubTitle(textRsc = R.string.post_project_description)
    Spacer(modifier = Modifier.height(8.dp))
    PostMultiLineTextField(
        modifier = modifier,
        text = text,
        onValueChange = onValueChange,
        placeHolderRsc = R.string.post_project_description_placeholder,
        textSize = 16,
        placeHolderTextSize = 16,
        imeOption = ImeAction.Done
    )
}