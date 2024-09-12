package com.gurumlab.wish.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
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
            .padding(16.dp)
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