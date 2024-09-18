package com.gurumlab.wish.ui.detail

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.DetailDescription
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomIconButton

@Composable
fun ProjectDescriptionArea(
    scrollState: ScrollState,
    wish: Wish
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 24.dp, end = 24.dp)
    ) {
        ProjectTitle(wish.title)
        Spacer(modifier = Modifier.height(16.dp))
        ProjectDescription(wish.simpleDescription)
        Spacer(modifier = Modifier.height(16.dp))
        FeatureList(wish.features)
        Spacer(modifier = Modifier.height(16.dp))
        DetailFeatureDescription(wish.detailDescription)
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
    Text(
        text = description,
        fontSize = 16.sp,
        color = Color.White
    )
}

@Composable
fun FeatureList(featureList: List<String>) {
    Text(
        text = stringResource(R.string.representative_feature_title),
        fontSize = 16.sp,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(2.dp))
    featureList.forEachIndexed { index, feature ->
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "${index + 1}. $feature",
            fontSize = 16.sp,
            color = Color.White
        )
    }
}

@Composable
fun DetailFeatureDescription(detailFeatureList: List<DetailDescription>) {
    detailFeatureList.forEach { detailDescription ->
        Text(
            text = detailDescription.feature,
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = detailDescription.description,
            fontSize = 16.sp,
            color = Color.White,
        )
        Spacer(modifier = Modifier.height(8.dp))
        detailDescription.photos.forEach {
            Image(
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(id = it),
                contentDescription = stringResource(R.string.wish_image),
                contentScale = ContentScale.Inside
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DetailScreenButtonArea(modifier: Modifier = Modifier) {
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
                text = stringResource(R.string.start),
                icon = R.drawable.ic_magic,
                description = stringResource(R.string.btn_begin),
                onClick = {})
            CustomIconButton(
                text = stringResource(R.string.like),
                icon = R.drawable.ic_message_enabled,
                description = stringResource(R.string.btn_message),
                onClick = {})
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}