package com.gurumlab.wish.ui.progressForDeveloper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.theme.defaultBoxColor
import com.gurumlab.wish.ui.util.CustomIconButton

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
fun PeriodOfProgress(wish: Wish) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        val startYear = wish.startedDate / 10000
        val startMonth = wish.startedDate % 10000 / 100
        val startDay = wish.startedDate % 100
        val (endYear, endMonth, endDay) = if (wish.completedDate == 0) {
            Triple(0, 0, 0)
        } else {
            Triple(
                wish.completedDate / 10000,
                (wish.completedDate % 10000) / 100,
                wish.completedDate % 100
            )
        }
        val startDate = "${startYear}.${startMonth}.${startDay}"
        val endDate =
            if (wish.completedDate == 0) stringResource(R.string.current) else "${endYear}.${endMonth}.${endDay}"

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
    modifier: Modifier = Modifier,
    wish: Wish,
    onSubmitScreen: (Wish) -> Unit,
    onMessageScreen: (Wish) -> Unit
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
                onClick = { onSubmitScreen(wish) })
            CustomIconButton(
                text = stringResource(R.string.message),
                icon = R.drawable.ic_message_enabled,
                description = stringResource(R.string.btn_message),
                onClick = { onMessageScreen(wish) })
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}