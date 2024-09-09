package com.gurumlab.wish.data.model

import android.media.Image

data class DetailDescription(
    val feature: String,
    val description: String,
    val photos: List<Image>
)