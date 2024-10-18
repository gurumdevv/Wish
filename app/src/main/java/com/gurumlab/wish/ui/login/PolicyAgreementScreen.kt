package com.gurumlab.wish.ui.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.ErrorSnackBar
import com.gurumlab.wish.ui.util.showSnackbar

@Composable
fun PolicyAgreementScreen(
    viewModel: LoginViewModel,
    onHomeScreen: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PolicyAgreementContent(
            viewModel = viewModel,
            onHomeScreen = onHomeScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun PolicyAgreementContent(
    viewModel: LoginViewModel,
    onHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val oneTapSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.signInWithGoogle(result.data)
        } else {
            viewModel.notifyIsLoginError()
            Log.d("LoginScreen", "One Tap Sign-In failed")
        }
    }

    val legacySignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.signInWithGoogle(result.data)
        } else {
            viewModel.notifyIsLoginError()
            Log.d("LoginScreen", "Legacy Sign-In failed")
        }
    }

    val onLoginProcess: () -> Unit =
        { viewModel.setSignInRequest(oneTapSignInLauncher, legacySignInLauncher, context) }

    Box(
        modifier = modifier
    ) {
        Column {
            AgreementTitle(
                textRsc = R.string.policy_agreement,
                fontSize = 24,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            AgreementTitle(textRsc = R.string.policy_agreement_content, fontSize = 14)
            Spacer(modifier = Modifier.weight(1f))
            AgreementButtonsField(
                isAllChecked = viewModel.agreementState.isAllChecked,
                isAgeChecked = viewModel.agreementState.isAgeChecked,
                isTermsChecked = viewModel.agreementState.isTermsChecked,
                isPrivacyChecked = viewModel.agreementState.isPrivacyChecked,
                onAllCheckedChange = {
                    viewModel.updateAgreementState(
                        ageChecked = it,
                        termsChecked = it,
                        privacyChecked = it
                    )
                },
                onAgeCheckedChange = { viewModel.updateAgreementState(ageChecked = it) },
                onTermsCheckedChange = { viewModel.updateAgreementState(termsChecked = it) },
                onPrivacyCheckedChange = { viewModel.updateAgreementState(privacyChecked = it) },
                onConfirm = onLoginProcess,
                isOnGoing = uiState.isOnGoingSignIn,
                context = context
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        ErrorSnackBar(
            snackbarHostState = snackbarHostState, modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        )
    }

    if (uiState.isLoginError) {
        viewModel.showSnackbarMessage(R.string.login_error)
    }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onHomeScreen()
        }
    }

    LaunchedEffect(viewModel.snackbarMessageRes.value) {
        showSnackbar(
            snackbarMessageRes = viewModel.snackbarMessageRes.value,
            context = context,
            snackbarHostState = snackbarHostState
        )
    }
}