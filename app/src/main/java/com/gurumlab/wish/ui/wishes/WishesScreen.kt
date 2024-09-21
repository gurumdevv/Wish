package com.gurumlab.wish.ui.wishes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.R
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.theme.backgroundColor
import com.gurumlab.wish.ui.util.CustomExceptionScreen
import com.gurumlab.wish.ui.util.CustomTopAppBar

@Composable
fun WishesScreen(viewModel: WishesViewModel, onDetailScreen: (wish: Wish, wishId: String) -> Unit) {
    WishesContent(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        viewModel = viewModel,
        onDetailScreen = onDetailScreen
    )
}

@Composable
fun WishesContent(
    modifier: Modifier = Modifier,
    viewModel: WishesViewModel,
    onDetailScreen: (wish: Wish, wishId: String) -> Unit
) {
    val wishes = viewModel.wishes.collectAsStateWithLifecycle()
    val wishesSortedByLikes = viewModel.wishesSortedByLikes.collectAsStateWithLifecycle()
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle()
    val isError = viewModel.isError.collectAsStateWithLifecycle()
    val isException = viewModel.isException.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        CustomTopAppBar(text = stringResource(R.string.wishes_top_bar))

        if (isLoading.value) {
            WishesLoadingScreen()
        } else {
            if (isException.value) {
                CustomExceptionScreen {
                    viewModel.loadWishes()
                    viewModel.loadWishesSortedByLikes()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        WishesBanner(wishes.value)
                        Spacer(modifier = Modifier.height(16.dp))
                        WishesSortByLikesTitle()
                        Spacer(modifier = Modifier.height(16.dp))
                        WishesSortByLikes(wishesSortedByLikes.value, onDetailScreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        WishesRandomTitle()
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(wishes.value.keys.size) { index ->
                        WishesRandomItem(
                            wish = wishes.value.values.elementAt(index),
                            wishId = wishes.value.keys.elementAt(index),
                            onDetailScreen = onDetailScreen
                        )
                    }
                }
            }
        }
    }
}