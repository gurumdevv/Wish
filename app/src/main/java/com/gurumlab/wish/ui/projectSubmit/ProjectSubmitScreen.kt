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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.CustomWideButton
import kotlinx.coroutines.launch

@Composable
fun ProjectSubmitScreen(
    viewModel: ProjectSubmitViewModel,
    minimizedWish: MinimizedWish,
    wishId: String,
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var repositoryInfo by remember { mutableStateOf("") }
    var accountInfo by remember { mutableStateOf("") }
    var accountOwner by remember { mutableStateOf("") }
    val isSubmitSuccess = viewModel.isSubmitSuccess.collectAsStateWithLifecycle()
    val isUpdateSuccess = viewModel.isUpdateSuccess.collectAsStateWithLifecycle()
    var isLoading by remember { mutableStateOf(false) }

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
            RepositoryInfoInput(repositoryInfo) { repositoryInfo = it }
            Spacer(modifier = Modifier.height(2.dp))
            RepositoryInfoInputHint()
            Spacer(modifier = Modifier.height(6.dp))
            AccountInfoTitle()
            Spacer(modifier = Modifier.height(8.dp))
            AccountInfoInput(accountInfo) { accountInfo = it }
            Spacer(modifier = Modifier.height(16.dp))
            AccountOwnerTitle()
            Spacer(modifier = Modifier.height(8.dp))
            AccountOwnerInput(accountOwner) { accountOwner = it }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CustomWideButton(
                text = stringResource(R.string.submit)
            ) {
                if (repositoryInfo.isNotBlank() && accountInfo.isNotBlank() && accountOwner.isNotBlank()) {
                    isLoading = true
                    viewModel.submitWish(
                        minimizedWish = minimizedWish,
                        repositoryURL = repositoryInfo,
                        accountInfo = accountInfo,
                        accountOwner = accountOwner
                    )
                    viewModel.updateWishStatusToComplete(wishId)
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

        if (isLoading) {
            ProjectSubmitLoadingScreen(
                Modifier
                    .size(130.dp)
                    .align(Alignment.Center)
            )
        }
    }
}