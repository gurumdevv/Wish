package com.gurumlab.wish.ui.wishes

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.util.CustomTopAppBar

@Composable
fun WishesRoute(
    bottomNavigationBar: @Composable () -> Unit,
    onDetailScreen: (wishId: String) -> Unit
) {
    val viewModel = hiltViewModel<WishesViewModel>()
    val topBar: @Composable () -> Unit = {
        CustomTopAppBar(
            stringResource(id = R.string.wishes_top_bar)
        )
    }
    WishesScreen(
        viewModel = viewModel,
        onDetailScreen = onDetailScreen,
        topBar = topBar,
        bottomBar = bottomNavigationBar
    )
}