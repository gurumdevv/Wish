package com.gurumlab.wish.ui.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
    onTermsAndCondition: () -> Unit
) {
    SettingsScreen(
        viewModel = viewModel,
        onAccountSetting = onAccountSetting,
        onMyProjectSetting = onMyProjectSetting,
        onApproachingProjectSetting = onApproachingProjectSetting,
        onTermsAndCondition = onTermsAndCondition
    )
}

@Composable
fun AccountSettingRoute(viewModel: SettingsViewModel, onStartScreen: () -> Unit) {
    AccountSettingScreen(viewModel = viewModel, onStartScreen = onStartScreen)
}

@Composable
fun ApproachingProjectSettingRoute(viewModel: SettingsViewModel) {
    ApproachingProjectSettingScreen(viewModel)
}

@Composable
fun MyProjectSettingRoute(viewModel: SettingsViewModel) {
    MyProjectSettingScreen(viewModel)
}

@Composable
fun TermsAndConditionRoute() {
    TermsAndConditionScreen()
}