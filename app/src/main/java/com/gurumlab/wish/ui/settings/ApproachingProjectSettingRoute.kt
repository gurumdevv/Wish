package com.gurumlab.wish.ui.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ApproachingProjectSettingRoute() {
    val viewModel = hiltViewModel<ApproachingProjectSettingViewModel>()
    ApproachingProjectSettingScreen(viewModel)
}