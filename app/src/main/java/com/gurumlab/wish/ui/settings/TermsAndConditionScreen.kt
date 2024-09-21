package com.gurumlab.wish.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun TermsAndConditionScreen() {
    TermsAndConditionContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 24.dp, end = 24.dp)
    )
}

@Composable
fun TermsAndConditionContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        TermsAndConditionTitle()
        Spacer(modifier = Modifier.height(16.dp))
        TermsAndConditionItem(textRsc = R.string.terms_of_service) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL.TERMS_AND_CONDITION))
            context.startActivity(intent)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TermsAndConditionItem(textRsc = R.string.privacy_policy) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(URL.PRIVACY_POLICY))
            context.startActivity(intent)
        }
    }
}

@Composable
fun TermsAndConditionTitle(
) {
    Text(
        text = stringResource(R.string.terms_and_condition),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
fun TermsAndConditionItem(
    textRsc: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(textRsc),
            fontSize = 18.sp,
            color = Color.White
        )
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.ic_arrow),
            contentDescription = stringResource(
                R.string.terms_and_condition_more_info
            ),
            tint = Color.White
        )
    }
}

object URL {
    const val TERMS_AND_CONDITION =
        "https://firebase.google.com/terms/" //TODO("서비스 이용 약관 작성후 URL 변경하기")
    const val PRIVACY_POLICY =
        "https://firebase.google.com/support/privacy/" //TODO("개인정보처리방침 작성후 URL 변경하기")
}