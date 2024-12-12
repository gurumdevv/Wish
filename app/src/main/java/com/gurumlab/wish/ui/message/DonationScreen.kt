package com.gurumlab.wish.ui.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.CompletedWish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomLottieLoader
import com.gurumlab.wish.ui.util.CustomWideButton
import com.gurumlab.wish.ui.util.DateTimeConverter
import kotlinx.coroutines.delay

@Composable
fun DonationScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    viewModel: SubmissionViewModel,
    completedWishId: String,
    onClosed: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.initializeData(completedWishId)
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        DonationContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            viewModel = viewModel,
            onClick = onClosed
        )
    }
}

@Composable
fun DonationContent(
    modifier: Modifier = Modifier,
    viewModel: SubmissionViewModel,
    onClick: () -> Unit
) {
    val completedWish = viewModel.completedWish.collectAsStateWithLifecycle()
    var isShowList by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(180)
        isShowList = true
    }

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = isShowList,
        enter = fadeIn(animationSpec = tween(durationMillis = 100, easing = Ease))
    ) {
        if (completedWish.value == null) {
            DonationLoadingScreen(modifier = modifier)
        } else {
            Column(
                modifier = modifier
            ) {
                ProjectTitle(completedWish.value!!.title)
                Spacer(modifier = Modifier.height(8.dp))
                ExplanationThanks(completedWish.value!!)
                Spacer(modifier = Modifier.height(8.dp))
                PeriodOfProgressTitle()
                Spacer(modifier = Modifier.height(8.dp))
                PeriodOfProgress(completedWish.value!!)
                Spacer(modifier = Modifier.height(16.dp))
                AccountOwnerTitle()
                Spacer(modifier = Modifier.height(8.dp))
                AccountInfoText(text = completedWish.value!!.accountOwner)
                Spacer(modifier = Modifier.height(16.dp))
                AccountInfoTitle()
                Spacer(modifier = Modifier.height(8.dp))
                AccountInfoText(text = completedWish.value!!.accountInfo)
                Spacer(modifier = Modifier.weight(1f))
                CustomWideButton(text = stringResource(R.string.close), onClick = onClick)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ProjectTitle(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ExplanationThanks(completedWish: CompletedWish) {
    Text(
        text = stringResource(R.string.thanks_explanation_donation, completedWish.developerName),
        fontSize = 13.sp,
        color = Color.White
    )
}

@Composable
fun PeriodOfProgressTitle() {
    Text(
        text = stringResource(R.string.period_of_progress),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PeriodOfProgress(completedWish: CompletedWish) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        val startYear = completedWish.startedDate / 10000
        val startMonth = completedWish.startedDate % 10000 / 100
        val startDay = completedWish.startedDate % 100
        val currentDate = DateTimeConverter.getCurrentDate()
        val endYear = currentDate / 10000
        val endMonth = currentDate % 10000 / 100
        val endDay = currentDate % 100
        val startDate = "${startYear}.${startMonth}.${startDay}"
        val endDate = "${endYear}.${endMonth}.${endDay}"

        Text(
            text = "$startDate ~ $endDate",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun AccountInfoTitle() {
    Text(
        text = stringResource(R.string.acoount_number),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AccountOwnerTitle() {
    Text(
        text = stringResource(R.string.acoount_owner_name),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AccountInfoText(
    text: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun DonationLoadingScreen(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CustomLottieLoader(
            modifier = Modifier.size(130.dp),
            resId = R.raw.animation_default_loading
        )
    }
}