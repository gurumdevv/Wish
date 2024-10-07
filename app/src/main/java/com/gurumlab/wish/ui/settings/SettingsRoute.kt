package com.gurumlab.wish.ui.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.util.CustomTopAppBar
import com.gurumlab.wish.ui.util.CustomTopAppBarWithButton

@Composable
fun SettingsRoute(
    bottomNavigationBar: @Composable () -> Unit,
    viewModel: SettingsViewModel,
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
    onTermsAndCondition: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBar(
            stringResource(id = R.string.settings_top_bar)
        )
    }
    SettingsScreen(
        topBar = topBar,
        bottomBar = bottomNavigationBar,
        viewModel = viewModel,
        onAccountSetting = onAccountSetting,
        onMyProjectSetting = onMyProjectSetting,
        onApproachingProjectSetting = onApproachingProjectSetting,
        onTermsAndCondition = onTermsAndCondition
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingRoute(
    viewModel: SettingsViewModel,
    onNavUp: () -> Unit,
    onStartScreen: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    AccountSettingScreen(
        topBar = topBar,
        viewModel = viewModel,
        onStartScreen = onStartScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproachingProjectSettingRoute(
    viewModel: SettingsViewModel,
    onNavUp: () -> Unit
) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    ApproachingProjectSettingScreen(topBar = topBar, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectSettingRoute(viewModel: SettingsViewModel, onNavUp: () -> Unit) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    MyProjectSettingScreen(topBar = topBar, viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionRoute(onNavUp: () -> Unit) {
    val topBar: @Composable () -> Unit = {
        CustomTopAppBarWithButton(
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState()),
            onNavIconPressed = onNavUp
        )
    }
    TermsAndConditionScreen(topBar = topBar)
}