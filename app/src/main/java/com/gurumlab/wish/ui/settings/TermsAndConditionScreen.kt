package com.gurumlab.wish.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.URL

@Composable
fun TermsAndConditionScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        TermsAndConditionContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
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