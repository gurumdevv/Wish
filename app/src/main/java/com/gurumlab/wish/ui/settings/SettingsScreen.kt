package com.gurumlab.wish.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomTopAppBar

@Composable
fun SettingsScreen(
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        CustomTopAppBar(stringResource(R.string.settings_top_bar))
        SettingsContent(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp),
            onAccountSetting = onAccountSetting,
            onMyProjectSetting = onMyProjectSetting,
            onApproachingProjectSetting = onApproachingProjectSetting,
        )
    }
}

@Composable
fun SettingsContent(
    modifier: Modifier = Modifier,
    onAccountSetting: () -> Unit,
    onMyProjectSetting: () -> Unit,
    onApproachingProjectSetting: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
    ) {
        UserInfo()
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
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL.TERMS_AND_CONDITION))
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(
                topStart = 10.dp,
                topEnd = 10.dp,
                bottomStart = 10.dp,
                bottomEnd = 10.dp
            )
        )
    }
}

object URL {
    const val TERMS_AND_CONDITION = "https://firebase.google.com/terms/" //TODO("이용약관 작성후 URL 변경하기")
}