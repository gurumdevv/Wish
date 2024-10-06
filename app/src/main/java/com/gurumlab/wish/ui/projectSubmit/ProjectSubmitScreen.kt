package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomWideButton

@Composable
fun ProjectSubmitScreen(
    viewModel: ProjectSubmitViewModel,
    wishId: String,
    minimizedWish: MinimizedWish,
    onComplete: () -> Unit
) {
    ProjectSubmitContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        viewModel = viewModel,
        minimizedWish = minimizedWish,
        wishId = wishId,
        onComplete = onComplete
    )
}

@Composable
fun ProjectSubmitContent(
    modifier: Modifier = Modifier,
    viewModel: ProjectSubmitViewModel,
    minimizedWish: MinimizedWish,
    wishId: String,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isSubmitSuccess = viewModel.isSubmitSuccess.collectAsStateWithLifecycle()
    val isUpdateSuccess = viewModel.isUpdateSuccess.collectAsStateWithLifecycle()

    if (isSubmitSuccess.value && isUpdateSuccess.value) onComplete()

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            ProjectTitle(minimizedWish.title)
            Spacer(modifier = Modifier.height(8.dp))
            ExplanationThanks(minimizedWish)
            Spacer(modifier = Modifier.height(8.dp))
            PeriodOfProgressTitle()
            Spacer(modifier = Modifier.height(8.dp))
            PeriodOfProgress(minimizedWish)
            Spacer(modifier = Modifier.height(16.dp))
            RepositoryInfoTitle()
            Spacer(modifier = Modifier.height(8.dp))
            RepositoryInfoInput(viewModel.repositoryInfo.value) { viewModel.setRepositoryInfo(it) }
            Spacer(modifier = Modifier.height(2.dp))
            RepositoryInfoInputHint()
            Spacer(modifier = Modifier.height(6.dp))
            AccountInfoTitle()
            Spacer(modifier = Modifier.height(8.dp))
            AccountInfoInput(viewModel.accountInfo.value) { viewModel.setAccountInfo(it) }
            Spacer(modifier = Modifier.height(16.dp))
            AccountOwnerTitle()
            Spacer(modifier = Modifier.height(8.dp))
            AccountOwnerInput(viewModel.accountOwner.value) { viewModel.setAccountOwner(it) }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CustomWideButton(
                text = stringResource(R.string.submit)
            ) {
                viewModel.submitWish(
                    wishId = wishId,
                    minimizedWish = minimizedWish,
                    snackbarMessageRes = R.string.blank
                )
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

        if (viewModel.isLoading.value) {
            ProjectSubmitLoadingScreen(
                Modifier
                    .size(130.dp)
                    .align(Alignment.Center)
            )
        }

        LaunchedEffect(viewModel.snackbarMessage.value) {
            viewModel.snackbarMessage.value?.let {
                snackbarHostState.showSnackbar(
                    message = context.getString(it),
                    duration = SnackbarDuration.Short
                )
                viewModel.resetSnackbarMessageState()
            }
        }
    }
}