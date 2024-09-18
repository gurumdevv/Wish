package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun ProgressForDeveloperScreen(wish: Wish, onSubmitScreen: (Wish) -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        ProgressForDeveloperContent(
            wish = wish,
            scrollState = scrollState,
            onSubmitScreen = { onSubmitScreen(wish) },
            onMessageScreen = {}
        )
    }
}

@Composable
fun ProgressForDeveloperContent(
    wish: Wish,
    scrollState: ScrollState,
    onSubmitScreen: (Wish) -> Unit,
    onMessageScreen: (Wish) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ProjectProgressDescriptionArea(wish, scrollState)
        ProgressForDeveloperScreenButtonArea(
            modifier = Modifier.align(Alignment.BottomCenter),
            wish = wish,
            onSubmitScreen = { onSubmitScreen(wish) },
            onMessageScreen = { onMessageScreen(wish) }
        )
    }
}

@Composable
fun ProjectProgressDescriptionArea(
    wish: Wish,
    scrollState: ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 24.dp, end = 24.dp)
    ) {
        ProjectTitle(wish.title)
        Spacer(modifier = Modifier.height(16.dp))
        ProjectDescription(wish.simpleDescription)
        Spacer(modifier = Modifier.height(16.dp))
        PeriodOfProgressTitle()
        Spacer(modifier = Modifier.height(8.dp))
        PeriodOfProgress(wish)
        Spacer(modifier = Modifier.height(16.dp))
        WishMakerTitle()
        Spacer(modifier = Modifier.height(8.dp))
        WishMaker(wish.posterName)
        Spacer(modifier = Modifier.height(78.dp))
    }
}