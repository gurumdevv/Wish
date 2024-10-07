package com.gurumlab.wish.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gurumlab.wish.R
import com.gurumlab.wish.ui.util.CustomTopAppBar

@Composable
fun HomeRoute(
    bottomNavigationBar: @Composable () -> Unit,
    onDetailScreen: (wishId: String) -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val topBar: @Composable () -> Unit = {
        CustomTopAppBar(
            stringResource(id = R.string.wish)
        )
    }
    HomeScreen(
        topBar = topBar,
        bottomBar = bottomNavigationBar,
        viewModel = viewModel,
        onDetailScreen = onDetailScreen
    )
}