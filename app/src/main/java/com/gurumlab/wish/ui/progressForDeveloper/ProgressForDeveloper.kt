package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomIconButton

@Composable
fun ProjectProgressDescriptionArea(
    minimizedWish: MinimizedWish,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 24.dp, end = 24.dp)
    ) {
        ProjectTitle(minimizedWish.title)
        Spacer(modifier = Modifier.height(16.dp))
        ProjectDescription(minimizedWish.simpleDescription)
        Spacer(modifier = Modifier.height(16.dp))
        PeriodOfProgressTitle()
        Spacer(modifier = Modifier.height(8.dp))
        PeriodOfProgress(minimizedWish)
        Spacer(modifier = Modifier.height(16.dp))
        WishMakerTitle()
        Spacer(modifier = Modifier.height(8.dp))
        WishMaker(minimizedWish.posterName)
        Spacer(modifier = Modifier.height(78.dp))
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
fun ProjectDescription(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Text(
            text = description,
            fontSize = 16.sp,
            color = Color.White
        )
    }
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
        val (endYear, endMonth, endDay) = if (minimizedWish.completedDate == 0) {
            Triple(0, 0, 0)
        } else {
            Triple(
                minimizedWish.completedDate / 10000,
                (minimizedWish.completedDate % 10000) / 100,
                minimizedWish.completedDate % 100
            )
        }
        val startDate = "${startYear}.${startMonth}.${startDay}"
        val endDate =
            if (minimizedWish.completedDate == 0) stringResource(R.string.current) else "${endYear}.${endMonth}.${endDay}"

        Text(
            text = "$startDate ~ $endDate",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun WishMakerTitle() {
    Text(
        text = stringResource(R.string.wish_maker),
        fontSize = 18.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun WishMaker(wishMaker: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Text(
            text = wishMaker,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun ProgressForDeveloperScreenButtonArea(
    onSubmitScreen: () -> Unit,
    onMessageScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomIconButton(
                text = stringResource(R.string.finish),
                icon = R.drawable.ic_clap,
                description = stringResource(R.string.btn_begin),
                onClick = onSubmitScreen
            )
            CustomIconButton(
                text = stringResource(R.string.message),
                icon = R.drawable.ic_message_enabled,
                description = stringResource(R.string.btn_message),
                onClick = onMessageScreen
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}