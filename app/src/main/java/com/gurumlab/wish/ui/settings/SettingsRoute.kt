package com.gurumlab.wish.ui.settings

import androidx.compose.material3.ExperimentalMaterial3Api
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
        viewModel = viewModel,
        onAccountSetting = onAccountSetting,
        onMyProjectSetting = onMyProjectSetting,
        onApproachingProjectSetting = onApproachingProjectSetting,
        onTermsAndCondition = onTermsAndCondition,
        topBar = topBar,
        bottomBar = bottomNavigationBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingRoute(
    viewModel: SettingsViewModel,
    onNavUp: () -> Unit,
    onStartScreen: () -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    AccountSettingScreen(
        viewModel = viewModel,
        onStartScreen = onStartScreen,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApproachingProjectSettingRoute(
    viewModel: SettingsViewModel,
    onNavUp: () -> Unit,
    onDetailScreen: (String) -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    ApproachingProjectSettingScreen(
        viewModel = viewModel,
        onDetailScreen = onDetailScreen,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectSettingRoute(
    viewModel: SettingsViewModel,
    onNavUp: () -> Unit,
    onDetailScreen: (String) -> Unit
) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    MyProjectSettingScreen(
        viewModel = viewModel,
        onDetailScreen = onDetailScreen,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionRoute(onNavUp: () -> Unit) {
    val topBar: @Composable () -> Unit = { CustomTopAppBarWithButton(onNavIconPressed = onNavUp) }
    TermsAndConditionScreen(topBar = topBar)
}