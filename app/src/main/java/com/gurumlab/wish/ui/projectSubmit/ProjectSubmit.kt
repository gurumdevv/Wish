package com.gurumlab.wish.ui.projectSubmit

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.gurumlab.wish.ui.util.CustomLoadingScreen
import com.gurumlab.wish.ui.util.CustomTextField
import com.gurumlab.wish.ui.util.CustomWideButton
import com.gurumlab.wish.ui.util.DateTimeConverter

@Composable
fun ProjectSubmitInputArea(
    minimizedWish: MinimizedWish,
    projectSubmitInputFieldUiState: ProjectSubmitInputFieldUiState,
    scrollState: ScrollState,
    onRepositoryInfoChange: (String) -> Unit,
    onAccountInfoChange: (String) -> Unit,
    onAccountOwnerChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        ProjectTitle(title = minimizedWish.title)
        Spacer(modifier = Modifier.height(8.dp))
        ExplanationThanks(minimizedWish = minimizedWish)
        Spacer(modifier = Modifier.height(8.dp))
        PeriodOfProgressTitle()
        Spacer(modifier = Modifier.height(8.dp))
        PeriodOfProgress(minimizedWish = minimizedWish)
        Spacer(modifier = Modifier.height(16.dp))
        RepositoryInfoTitle()
        Spacer(modifier = Modifier.height(8.dp))
        RepositoryInfoInput(
            text = projectSubmitInputFieldUiState.repositoryInfo,
            onValueChange = onRepositoryInfoChange
        )
        Spacer(modifier = Modifier.height(2.dp))
        RepositoryInfoInputHint()
        Spacer(modifier = Modifier.height(6.dp))
        AccountInfoTitle()
        Spacer(modifier = Modifier.height(8.dp))
        AccountInfoInput(
            text = projectSubmitInputFieldUiState.accountInfo,
            onValueChange = onAccountInfoChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        AccountOwnerTitle()
        Spacer(modifier = Modifier.height(8.dp))
        AccountOwnerInput(
            text = projectSubmitInputFieldUiState.accountOwner,
            onValueChange = onAccountOwnerChange
        )
    }
}

@Composable
fun ProjectSubmitButtonArea(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        CustomWideButton(
            text = stringResource(R.string.submit),
            onClick = onClick
        )
        Spacer(modifier = Modifier.size(24.dp))
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
fun ProjectSubmitLoadingScreen(modifier: Modifier = Modifier) {
    CustomLoadingScreen(modifier)
}