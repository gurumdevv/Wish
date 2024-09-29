package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomLottieLoader
import com.gurumlab.wish.ui.util.CustomTextField
import com.gurumlab.wish.ui.util.DateTimeConverter

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
fun ExplanationThanks(minimizedWish: MinimizedWish) {
    Text(
        text = stringResource(R.string.thanks_explanation, minimizedWish.posterName),
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
fun PeriodOfProgress(minimizedWish: MinimizedWish) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        val startYear = minimizedWish.startedDate / 10000
        val startMonth = minimizedWish.startedDate % 10000 / 100
        val startDay = minimizedWish.startedDate % 100
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
fun RepositoryInfoTitle() {
    Text(
        text = stringResource(R.string.repository_url),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RepositoryInfoInput(
    text: String,
    onValueChange: (String) -> Unit
) {
    CustomTextField(
        text = text,
        onValueChange = onValueChange,
        placeholderText = stringResource(R.string.github_url),
        fontSize = 16,
        placeholderTextSize = 16,
        imeOption = ImeAction.Next
    )
}

@Composable
fun RepositoryInfoInputHint() {
    Text(
        text = stringResource(R.string.repsitory_hint),
        fontSize = 8.sp,
        color = Color.White,
    )
}

@Composable
fun AccountInfoTitle() {
    Text(
        text = stringResource(R.string.acoount_info),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AccountInfoInput(
    text: String,
    onValueChange: (String) -> Unit
) {
    CustomTextField(
        text = text,
        onValueChange = onValueChange,
        placeholderText = stringResource(R.string.sample_account_info),
        fontSize = 16,
        placeholderTextSize = 16,
        imeOption = ImeAction.Next
    )
}

@Composable
fun AccountOwnerTitle() {
    Text(
        text = stringResource(R.string.acoount_owner),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun AccountOwnerInput(
    text: String,
    onValueChange: (String) -> Unit
) {
    CustomTextField(
        text = text,
        onValueChange = onValueChange,
        placeholderText = stringResource(R.string.sample_account_owner),
        fontSize = 16,
        placeholderTextSize = 16,
        imeOption = ImeAction.Done
    )
}

@Composable
fun ProjectSubmitLoadingScreen(modifier: Modifier) {
    CustomLottieLoader(modifier = modifier, resId = R.raw.animation_default_loading)
}