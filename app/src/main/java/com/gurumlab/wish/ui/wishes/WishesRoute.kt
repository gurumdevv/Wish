package com.gurumlab.wish.ui.wishes

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.data.model.Wish

@Composable
fun WishesRoute(onDetailScreen: (wishId: String) -> Unit) {
    val viewModel = hiltViewModel<WishesViewModel>()
    WishesScreen(viewModel = viewModel, onDetailScreen = onDetailScreen)
}