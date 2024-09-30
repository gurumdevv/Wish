package com.gurumlab.wish.ui.login

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginRoute(
    onPolicyAgreementRoute: () -> Unit
) {
    LoginScreen(onPolicyAgreementRoute)
}

@Composable
fun PolicyAgreementRoute() {
    val viewModel = hiltViewModel<LoginViewModel>()
    PolicyAgreementScreen(viewModel)
}