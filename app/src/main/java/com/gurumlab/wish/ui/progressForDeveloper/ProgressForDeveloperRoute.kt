package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.runtime.Composable
import com.gurumlab.wish.data.model.Wish

@Composable
fun ProgressForDeveloperRoute(wish: Wish, wishId: String, onSubmitScreen: (Wish, String) -> Unit) {
    ProgressForDeveloperScreen(wish = wish, wishId = wishId) { wishObject, wishIdString ->
        onSubmitScreen(wishObject, wishIdString)
    }
}