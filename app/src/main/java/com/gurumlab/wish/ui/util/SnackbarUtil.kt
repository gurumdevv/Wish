package com.gurumlab.wish.ui.util

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ErrorSnackBarMessage(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
    ) { data ->
        CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
    }
}

suspend fun showSnackbar(
    snackbarMessageRes: Int?,
    context: Context,
    snackbarHostState: SnackbarHostState,
) {
    snackbarMessageRes?.let {
        snackbarHostState.showSnackbar(
            message = context.getString(snackbarMessageRes),
            duration = SnackbarDuration.Short
        )
    }
}