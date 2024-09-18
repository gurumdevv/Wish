package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.Wish

@Composable
fun ProgressForDeveloperRoute(wish: Wish) {
    ProgressForDeveloperScreen(wish = wish) {
        //TODO("제출하기, 메세지 nav")
    }
}