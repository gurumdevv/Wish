package com.gurumlab.wish.ui.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRoute(
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
    onTermsAndCondition: () -> Unit
) {
    SettingsScreen(
        onAccountSetting = onAccountSetting,
        onMyProjectSetting = onMyProjectSetting,
        onApproachingProjectSetting = onApproachingProjectSetting,
        onTermsAndCondition = onTermsAndCondition
    )
}

@Composable
fun ApproachingProjectSettingRoute() {
    val viewModel = hiltViewModel<ApproachingProjectSettingViewModel>()
    ApproachingProjectSettingScreen(viewModel)
}

@Composable
fun MyProjectSettingRoute() {
    val viewModel = hiltViewModel<MyProjectSettingViewModel>()
    MyProjectSettingScreen(viewModel)
}

@Composable
fun TermsAndConditionRoute() {
    TermsAndConditionScreen()
}