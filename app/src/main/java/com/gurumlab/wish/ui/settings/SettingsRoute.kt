package com.gurumlab.wish.ui.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsRoute(
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
) {
    SettingsScreen(
        onAccountSetting = onAccountSetting,
        onMyProjectSetting = onMyProjectSetting,
        onApproachingProjectSetting = { },
        onTermsAndCondition = { }
    )
}