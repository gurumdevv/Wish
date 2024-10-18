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
import androidx.compose.ui.graphics.vector.ImageVector
import com.gurumlab.wish.ui.theme.yellow03

@Composable
fun ErrorSnackBar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CommonSnackbar(
        textColor = Color.White,
        backgroundColor = Color.Red,
        icon = Icons.Outlined.Warning,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun CautionSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    CommonSnackbar(
        textColor = Color.Black,
        backgroundColor = yellow03,
        icon = Icons.Outlined.Warning,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun CommonSnackbar(
    textColor: Color,
    backgroundColor: Color,
    icon: ImageVector,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
    ) { data ->
        CustomSnackbarContent(
            snackbarData = data,
            textColor = textColor,
            backgroundColor = backgroundColor,
            icon = icon
        )
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