package com.gurumlab.wish.ui.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun AccountSettingScreen(
    viewModel: SettingsViewModel,
    onStartScreen: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        AccountSettingContent(
            viewModel = viewModel,
            onStartScreen = onStartScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun AccountSettingContent(
    viewModel: SettingsViewModel,
    onStartScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.accountUiState.collectAsStateWithLifecycle()

    val oneTapSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.signInWithGoogle(result.data)
        } else {
            viewModel.notifyIsLoginError("One Tap Sign-In failed")
        }
    }

    val legacySignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.signInWithGoogle(result.data)
        } else {
            viewModel.notifyIsLoginError("Legacy Sign-In failed")
        }
    }

    Column(
        modifier = modifier
    ) {
        AccountSettingTitle()
        Spacer(modifier = Modifier.height(16.dp))
        AccountSubsetTitle(textRsc = R.string.log_out)
        Spacer(modifier = Modifier.height(8.dp))
        AccountSubsetButton(textRsc = R.string.log_out, textColor = Color.White) {
            viewModel.logOut()
        }
        Spacer(modifier = Modifier.height(16.dp))
        AccountSubsetTitle(textRsc = R.string.delete_account)
        Spacer(modifier = Modifier.height(8.dp))
        AccountSubsetButton(textRsc = R.string.delete_account, textColor = Color.Red) {
            viewModel.setSignInRequest(oneTapSignInLauncher, legacySignInLauncher, context)
        }
    }

    LaunchedEffect(uiState.isLogOut, uiState.isDeleteAccount) {
        if (uiState.isLogOut || uiState.isDeleteAccount) {
            onStartScreen()
        }
    }
}