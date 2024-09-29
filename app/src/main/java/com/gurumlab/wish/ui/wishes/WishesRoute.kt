package com.gurumlab.wish.ui.wishes

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WishesRoute(onDetailScreen: (wishId: String) -> Unit) {
    val viewModel = hiltViewModel<WishesViewModel>()
    WishesScreen(viewModel = viewModel, onDetailScreen = onDetailScreen)
}