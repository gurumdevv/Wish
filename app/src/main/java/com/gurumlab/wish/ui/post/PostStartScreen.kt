package com.gurumlab.wish.ui.post

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomWideButton
import com.gurumlab.wish.ui.util.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PostStartScreen(viewModel: PostViewModel, onPostDescription: () -> Unit) {
    PostStartContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp)
    )
}

@Composable
fun PostStartContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    var projectTitle by remember { mutableStateOf("") }
    var oneLineDescription by remember { mutableStateOf("") }
    var simpleDescription by remember { mutableStateOf("") }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            PostStartTitleSection()
            Spacer(modifier = Modifier.height(16.dp))
            PostProjectTitleSection(
                text = projectTitle,
                onValueChange = {
                    projectTitle = if (it.length <= 15) it else it.substring(0, 15)
                },
                currentTextLength = projectTitle.length,
                maxTextLength = 15
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostOneLineDescriptionSection(
                text = oneLineDescription,
                onValueChange = {
                    oneLineDescription = if (it.length <= 15) it else it.substring(0, 15)
                },
                currentTextLength = oneLineDescription.length,
                maxTextLength = 15
            )
            Spacer(modifier = Modifier.height(8.dp))
            PostSimpleDescriptionSection(
                text = simpleDescription,
                onValueChange = {
                    simpleDescription = if (it.length <= 30) it else it.substring(0, 30)
                },
                currentTextLength = simpleDescription.length,
                maxTextLength = 30,
                scope = scope,
                scrollState = scrollState
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CustomWideButton(
                text = stringResource(R.string.submit)
            ) {
                if (projectTitle.isNotBlank() && oneLineDescription.isNotBlank() && simpleDescription.isNotBlank()) {
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
fun PostStartTitleSection() {
    PostTitle(titleTextRsc = R.string.post_start_title)
    Spacer(modifier = Modifier.height(16.dp))
    PostTitleDescription(descriptionTextRsc = R.string.post_title_description)
}

@Composable
fun PostProjectTitleSection(
    text: String,
    onValueChange: (String) -> Unit,
    currentTextLength: Int,
    maxTextLength: Int
) {
    PostSubTitle(textRsc = R.string.post_project_title)
    Spacer(modifier = Modifier.height(8.dp))
    PostTextField(
        text = text,
        onValueChange = onValueChange,
        placeHolderRsc = R.string.post_project_title_placeholder,
        imeOption = ImeAction.Next
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
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
    onValueChange: (String) -> Unit,
    currentTextLength: Int,
    maxTextLength: Int
) {
    PostSubTitle(textRsc = R.string.post_one_line_description)
    Spacer(modifier = Modifier.height(8.dp))
    PostTextField(
        text = text,
        onValueChange = onValueChange,
        placeHolderRsc = R.string.post_one_line_description_placeholder,
        imeOption = ImeAction.Next
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
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
    onValueChange: (String) -> Unit,
    currentTextLength: Int,
    maxTextLength: Int,
    scope: CoroutineScope? = null,
    scrollState: ScrollState? = null,
) {
    val offsetDp = 18.sp.toDp()
    val density = LocalDensity.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    PostSubTitle(textRsc = R.string.post_simple_description)
    Spacer(modifier = Modifier.height(8.dp))
    PostTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .onGloballyPositioned { _ ->
                if (isFocused) {
                    scope?.launch {
                        scrollState?.scrollBy(with(density) { offsetDp.toPx() })
                    }
                }
            },
        text = text,
        onValueChange = onValueChange,
        placeHolderRsc = R.string.post_simple_description_placeholder,
        imeOption = ImeAction.Done,
        singleLine = false,
        minLines = 2
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        LimitTextLength(
            textLength = currentTextLength,
            maxLength = maxTextLength,
        )
    }
}