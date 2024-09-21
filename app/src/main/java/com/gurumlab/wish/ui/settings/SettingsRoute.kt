package com.gurumlab.wish.ui.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsRoute() {
    SettingsScreen(
        onAccountSetting = { },
        onMyProjectSetting = { },
        onApproachingProjectSetting = { },
        onTermsAndCondition = { }
    )
}