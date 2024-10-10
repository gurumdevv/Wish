package com.gurumlab.wish.ui.login

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@Composable
fun LoginRoute(
    onPolicyAgreementRoute: () -> Unit
) {
    LoginScreen(onPolicyAgreementRoute = onPolicyAgreementRoute)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolicyAgreementRoute(
    onNavUp: () -> Unit,
    onHomeScreen: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    val viewModel = hiltViewModel<LoginViewModel>()
    PolicyAgreementScreen(
        viewModel = viewModel,
        onHomeScreen = onHomeScreen,
        topBar = topBar
    )
}