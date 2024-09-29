package com.gurumlab.wish.ui.home

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.Wish

@Composable
fun HomeRoute(onDetailScreen: (wishId: String) -> Unit) {
    val viewModel = hiltViewModel<HomeViewModel>()
    HomeScreen(viewModel = viewModel, onDetailScreen = onDetailScreen)
}