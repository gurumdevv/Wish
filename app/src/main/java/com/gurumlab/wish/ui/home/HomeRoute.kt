package com.gurumlab.wish.ui.home

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeRoute() {
    val viewModel = hiltViewModel<HomeViewModel>()
    HomeScreen(viewModel = viewModel)
}