package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
    onTermsAndCondition: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        SettingsContent(
            viewModel = viewModel,
            onAccountSetting = onAccountSetting,
            onMyProjectSetting = onMyProjectSetting,
            onApproachingProjectSetting = onApproachingProjectSetting,
            onTermsAndCondition = onTermsAndCondition,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun SettingsContent(
    viewModel: SettingsViewModel,
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
    onTermsAndCondition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        UserInfo(viewModel.userInfo, context)
        Spacer(modifier = Modifier.height(16.dp))
        SettingsItem(
            text = stringResource(R.string.account_setting),
            onClick = { onAccountSetting() },
            shape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        )
        HorizontalDivider(color = Color.Black)
        SettingsItem(
            text = stringResource(R.string.my_project_setting),
            onClick = { onMyProjectSetting() },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            )
        )
        HorizontalDivider(color = Color.Black)
        SettingsItem(
            text = stringResource(R.string.approaching_project_setting),
            onClick = { onApproachingProjectSetting() },
            shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 10.dp,
                bottomEnd = 10.dp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        SettingsItem(
            text = stringResource(R.string.terms_and_condition),
            onClick = { onTermsAndCondition() },
            shape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
                bottomStart = 10.dp,
                bottomEnd = 10.dp
            )
        )
    }
}