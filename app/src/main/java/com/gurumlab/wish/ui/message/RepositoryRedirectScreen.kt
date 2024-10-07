package com.gurumlab.wish.ui.message

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent

@Composable
fun RepositoryRedirectScreen(
    viewModel: SubmissionViewModel,
    completedWishId: String,
    onBack: () -> Unit
) {
    viewModel.initializeData(completedWishId)

    RepositoryRedirectContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp),
        viewModel = viewModel,
        onBack = onBack
    )
}

@Composable
fun RepositoryRedirectContent(
    modifier: Modifier,
    viewModel: SubmissionViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.completedWish.collectAsStateWithLifecycle().value?.let {
        Log.d("Repository", it.repositoryURL)
        val repositoryURL = it.repositoryURL
        if (repositoryURL.isEmpty()) {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.invalid_url),
                    duration = SnackbarDuration.Short
                )
                onBack()
            }
        } else {
            val fixedURL = if (repositoryURL.startsWith("http://")) {
                repositoryURL.replaceFirst("http://", "https://")
            } else if (!repositoryURL.startsWith("https://")) {
                "https://$repositoryURL"
            } else {
                repositoryURL
            }

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixedURL))
            context.startActivity(intent)
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.moving),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

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