package com.gurumlab.wish.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.Gray00
import com.gurumlab.wish.ui.theme.Gray02
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.lightGreen00
import com.gurumlab.wish.ui.util.CustomSnackbarContent
import com.gurumlab.wish.ui.util.URL

@Composable
fun PolicyAgreementScreen(
    viewModel: LoginViewModel,
    onHomeScreen: () -> Unit,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        PolicyAgreementContent(
            viewModel = viewModel,
            onHomeScreen = onHomeScreen,
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
fun PolicyAgreementContent(
    viewModel: LoginViewModel,
    onHomeScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val agreementState by viewModel.agreementState
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val oneTapSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.signInWithGoogle(result.data)
        } else {
            viewModel.notifyIsLoginError()
            Log.d("LoginScreen", "One Tap Sign-In failed")
        }
    }

    val legacySignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.signInWithGoogle(result.data)
        } else {
            viewModel.notifyIsLoginError()
            Log.d("LoginScreen", "Legacy Sign-In failed")
        }
    }

    val onLoginProcess: () -> Unit =
        { viewModel.setSignInRequest(oneTapSignInLauncher, legacySignInLauncher, context) }

    Box(
        modifier = modifier
    ) {
        Column {
            AgreementTitle(
                textRsc = R.string.policy_agreement,
                fontSize = 24,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(8.dp))
            AgreementTitle(textRsc = R.string.policy_agreement_content, fontSize = 14)
            Spacer(modifier = Modifier.weight(1f))
            AgreementButtonsField(
                isAllChecked = agreementState.isAllChecked,
                isAgeChecked = agreementState.isAgeChecked,
                isTermsChecked = agreementState.isTermsChecked,
                isPrivacyChecked = agreementState.isPrivacyChecked,
                onAllCheckedChange = {
                    viewModel.onAgreementChange(
                        ageChecked = it,
                        termsChecked = it,
                        privacyChecked = it
                    )
                },
                onAgeCheckedChange = { viewModel.onAgreementChange(ageChecked = it) },
                onTermsCheckedChange = { viewModel.onAgreementChange(termsChecked = it) },
                onPrivacyCheckedChange = { viewModel.onAgreementChange(privacyChecked = it) },
                onConfirm = onLoginProcess,
                isOnGoing = uiState.isOnGoingSignIn,
                context = context
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-102).dp)
        ) { data ->
            CustomSnackbarContent(data, Color.Red, Color.White, Icons.Outlined.Warning)
        }

        if (uiState.isLoginSuccess) {
            onHomeScreen()
        }

        LaunchedEffect(uiState.isLoginError) {
            if (uiState.isLoginError) {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.login_error),
                    duration = SnackbarDuration.Short
                )
                viewModel.resetIsLoginError()
            }
        }
    }
}

@Composable
fun AgreementTitle(
    textRsc: Int,
    fontSize: Int,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Text(
        text = stringResource(textRsc),
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = Color.White
    )
}

@Composable
fun AgreementButtonsField(
    isAllChecked: Boolean,
    isAgeChecked: Boolean,
    isTermsChecked: Boolean,
    isPrivacyChecked: Boolean,
    onAllCheckedChange: (Boolean) -> Unit,
    onAgeCheckedChange: (Boolean) -> Unit,
    onTermsCheckedChange: (Boolean) -> Unit,
    onPrivacyCheckedChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    isOnGoing: Boolean,
    context: Context
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AllAgreeCheckBoxField(
            stringRsc = R.string.all_agree,
            isChecked = isAllChecked,
            onCheckedChange = onAllCheckedChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        CheckBoxField(
            stringRsc = R.string.age_check,
            isEssential = true,
            isChecked = isAgeChecked,
            onCheckedChange = onAgeCheckedChange,
            context = context
        )
        Spacer(modifier = Modifier.height(8.dp))
        CheckBoxField(
            stringRsc = R.string.terms_of_use_agreement,
            isEssential = true,
            isChecked = isTermsChecked,
            onCheckedChange = onTermsCheckedChange,
            context = context,
            isShowInfo = true,
            infoUrl = URL.TERMS_AND_CONDITION
        )
        Spacer(modifier = Modifier.height(8.dp))
        CheckBoxField(
            stringRsc = R.string.privacy_policy_agreement,
            isEssential = true,
            isChecked = isPrivacyChecked,
            onCheckedChange = onPrivacyCheckedChange,
            context = context,
            isShowInfo = true,
            infoUrl = URL.PRIVACY_POLICY,
        )
        Spacer(modifier = Modifier.height(16.dp))
        PolicyAgreementButton(
            stringRsc = R.string.confirm,
            onGoingRsc = R.string.ongoing,
            isEnabled = isAllChecked,
            isOnGoing = isOnGoing,
        ) { onConfirm() }
    }
}

@Composable
fun AllAgreeCheckBoxField(
    stringRsc: Int,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable { onCheckedChange(!isChecked) },
            painter = painterResource(
                id = if (isChecked) R.drawable.ic_all_check_enabled else R.drawable.ic_all_check_disabled
            ),
            tint = if (isChecked) lightGreen00 else Color.White,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = stringRsc),
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckBoxField(
    stringRsc: Int,
    isEssential: Boolean,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    context: Context,
    isShowInfo: Boolean = false,
    infoUrl: String = ""
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable { onCheckedChange(!isChecked) },
            painter = painterResource(id = R.drawable.ic_check),
            tint = if (isChecked) lightGreen00 else Color.White,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(id = stringRsc), fontSize = 14.sp, color = Color.White)
        if (isEssential) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.essential),
                fontSize = 14.sp,
                color = Color.White
            )
        }
        if (isShowInfo) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(infoUrl))
                        context.startActivity(intent)
                    },
                painter = painterResource(id = R.drawable.ic_info),
                contentDescription = null
            )
        }
    }
}

@Composable
fun PolicyAgreementButton(
    stringRsc: Int,
    onGoingRsc: Int,
    isEnabled: Boolean,
    isOnGoing: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled && !isOnGoing,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Gray00,
            disabledContentColor = Gray02
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = if (isOnGoing) onGoingRsc else stringRsc),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}