package com.gurumlab.wish.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.Gray00
import com.gurumlab.wish.ui.theme.Gray02
import com.gurumlab.wish.ui.theme.defaultScrimColor
import com.gurumlab.wish.ui.theme.lightGreen00
import com.gurumlab.wish.ui.util.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginStartButton(
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.start_loading_screen),
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomSheet(
    sheetState: SheetState,
    scope: CoroutineScope,
    onShowBottomChange: (Boolean) -> Unit,
    onPolicyAgreementRoute: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onShowBottomChange(false) },
        sheetState = sheetState,
        containerColor = Color.White,
        contentColor = Color.Black,
        scrimColor = defaultScrimColor
    ) {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.Black),
            onClick = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        onShowBottomChange(false)
                    }
                    onPolicyAgreementRoute()
                }
            }) {
            LoginButton(imageRsc = R.drawable.ic_google, textRsc = R.string.sign_in_google)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun LoginButton(
    imageRsc: Int,
    textRsc: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier
                .size(32.dp),
            painter = painterResource(id = imageRsc),
            contentDescription = null
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = textRsc),
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

// <-- PolicyAgreement -->
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