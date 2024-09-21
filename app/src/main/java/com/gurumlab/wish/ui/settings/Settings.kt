package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.theme.defaultBoxColor

@Composable
fun UserInfo() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "일하는 팽귄", //TODO("사용자 이름 가져오기")
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "working@panguin.com", //TODO("사용자 이메일 가져오기")
                fontSize = 16.sp,
                color = Color.White,
            )
        }
        Image(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            painter = painterResource(id = R.drawable.sample_profile_image), //TODO("사용자 이미지 가져오기")
            contentDescription = stringResource(R.string.profile_image),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SettingsItem(
    text: String,
    onClick: () -> Unit,
    shape: RoundedCornerShape
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .clip(shape)
            .background(defaultBoxColor)
            .padding(16.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.White,
        )
    }
}