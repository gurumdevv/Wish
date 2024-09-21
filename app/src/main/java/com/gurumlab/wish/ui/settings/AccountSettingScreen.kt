package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor

@Composable
fun AccountSettingScreen(
    onLogOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    AccountSettingContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp),
        onLogOut = onLogOut,
        onDeleteAccount = onDeleteAccount
    )
}

@Composable
fun AccountSettingContent(
    modifier: Modifier = Modifier,
    onLogOut: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        AccountSettingTitle()
        Spacer(modifier = Modifier.height(16.dp))
        AccountSubsetTitle(textRsc = R.string.log_out)
        Spacer(modifier = Modifier.height(8.dp))
        AccountSubsetButton(textRsc = R.string.log_out, textColor = Color.White) {
            onLogOut()
        }
        Spacer(modifier = Modifier.height(16.dp))
        AccountSubsetTitle(textRsc = R.string.delete_account)
        Spacer(modifier = Modifier.height(8.dp))
        AccountSubsetButton(textRsc = R.string.delete_account, textColor = Color.Red) {
            onDeleteAccount()
        }
    }
}

@Composable
fun AccountSettingTitle() {
    Text(
        text = stringResource(R.string.account_setting),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun AccountSubsetTitle(
    textRsc: Int
) {
    Text(
        text = stringResource(textRsc),
        fontSize = 18.sp,
        color = Color.White
    )
}

@Composable
fun AccountSubsetButton(
    textRsc: Int,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = defaultBoxColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        onClick = { onClick() }) {
        Row {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(textRsc),
                fontSize = 16.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}