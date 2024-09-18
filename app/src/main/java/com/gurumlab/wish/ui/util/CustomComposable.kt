package com.gurumlab.wish.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor

@Preview
@Composable
fun PreviewDefaultIconButton() {
    CustomIconButton(
        text = "시작해요",
        icon = R.drawable.ic_magic,
        description = "버튼 설명",
        onClick = {}
    )
}


@Composable
fun CustomIconButton(
    text: String,
    icon: Int,
    description: String,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val width = screenWidthDp.dp / 2 - 24.dp - 8.dp

    Button(
        modifier = Modifier
            .width(width)
            .height(62.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = defaultBoxColor,
            contentColor = Color.White
        ),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(28.dp),
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = description
            )
            Spacer(modifier = Modifier.width(8.dp))
            AutoSizeText(
                text = text,
                color = Color.White,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                maxTextSize = 20.sp
            )
        }
    }
}

@Composable
fun CustomTopAppBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 5.dp)
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = R.drawable.temp_logo),
            contentDescription = stringResource(
                R.string.logo
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.padding(top = 3.dp),
            text = "Wish",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            //No title
        },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onNavIconPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun CustomSnackbarContent(
    snackbarData: SnackbarData,
    backgroundColor: Color,
    textColor: Color,
    icon: ImageVector? = null
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.snackbar_icon),
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = snackbarData.visuals.message,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun CustomLottieLoader(modifier: Modifier = Modifier, resId: Int) {
    Box(modifier = modifier) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))
        LottieAnimation(composition, iterations = LottieConstants.IterateForever)
    }
}

@Composable
fun CustomExceptionScreen(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.no_internet_connection),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.please_check_internet_connection),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = defaultBoxColor,
                contentColor = Color.White
            ),
            onClick = { onClick() }
        ) {
            Row {
                Text(
                    text = stringResource(R.string.retry),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.White,
                )
            }
        }
    }
}