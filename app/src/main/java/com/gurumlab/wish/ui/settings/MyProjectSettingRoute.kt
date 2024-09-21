package com.gurumlab.wish.ui.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyProjectSettingRoute() {
    val viewModel = hiltViewModel<MyProjectSettingViewModel>()
    MyProjectSettingScreen(viewModel)
}