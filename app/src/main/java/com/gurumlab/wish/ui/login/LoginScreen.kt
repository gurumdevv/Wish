package com.gurumlab.wish.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.defaultScrimColor
import com.gurumlab.wish.ui.util.CustomGifImage
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onPolicyAgreementRoute: () -> Unit
) {
    LoginContent(
        modifier = Modifier
            .fillMaxSize(),
        onPolicyAgreementRoute = onPolicyAgreementRoute
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    modifier: Modifier = Modifier,
    onPolicyAgreementRoute: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        CustomGifImage(
            modifier = Modifier.fillMaxSize(),
            resId = R.drawable.image_shooting_star,
            contentDescriptionSrc = R.string.loading_image,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = stringResource(R.string.loading_screen_title),
                fontSize = 48.sp,
                lineHeight = 55.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = { showBottomSheet = true }) {
                Row {
                    Text(
                        text = stringResource(id = R.string.start_loading_screen),
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
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
                            showBottomSheet = false
                        }
                        onPolicyAgreementRoute()
                    }
                }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterStart),
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.sign_in_google),
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}