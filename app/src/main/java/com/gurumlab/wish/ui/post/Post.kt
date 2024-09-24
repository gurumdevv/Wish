package com.gurumlab.wish.ui.post

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.ui.theme.Gray01
import com.gurumlab.wish.ui.util.CustomTextField

@Composable
fun PostTitle(titleTextRsc: Int) {
    Text(
        text = stringResource(id = titleTextRsc),
        fontSize = 24.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PostSubTitle(textRsc: Int) {
    Text(
        text = stringResource(id = textRsc),
        fontSize = 20.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PostTextField(
    text: String,
    onValueChange: (String) -> Unit,
    placeHolderRsc: Int,
    imeOption: ImeAction = ImeAction.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    CustomTextField(
        text = text,
        onValueChange = onValueChange,
        placeholderText = stringResource(id = placeHolderRsc),
        fontSize = 16,
        placeholderTextSize = 12.sp,
        imeOption = imeOption,
        singleLine = singleLine,
        minLines = minLines
    )
}

@Composable
fun PostTitleDescription(descriptionTextRsc: Int) {
    Text(
        text = stringResource(id = descriptionTextRsc),
        fontSize = 16.sp,
        color = Color.White,
    )
}

@Composable
fun LimitTextLength(textLength: Int, maxLength: Int) {
    Row {
        Text(
            text = textLength.toString(),
            color = if (textLength < maxLength) Color.White else Color.Red,
            fontSize = 14.sp
        )
        Text(
            text = "/$maxLength",
            color = Gray01,
            fontSize = 14.sp
        )
    }
}