package com.gurumlab.wish.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gurumlab.wish.data.model.WishStatus
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun MyProjectSettingScreen(
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    viewModel: SettingsViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadMyWishes()
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar
    ) { innerPadding ->
        MyProjectSettingContent(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(start = 24.dp, end = 24.dp),
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProjectSettingContent(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.myProjectUiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentWishId by remember { mutableStateOf("") }

    when (uiState) {
        MyProjectUiState.Loading -> {
            MyProjectLoadingScreen(modifier)
        }

        MyProjectUiState.Empty -> {
            MyProjectWishesList(
                wishes = emptyMap(),
                totalCount = 0,
                successCount = 0,
                onItemClick = {},
                modifier = modifier
            )
        }

        MyProjectUiState.Exception -> {
            ProjectExceptionScreen(modifier)
        }

        is MyProjectUiState.Success -> {
            val wishes = (uiState as MyProjectUiState.Success).wishes
            val totalCount = wishes.keys.size
            val successCount = wishes.values.count { it.status == WishStatus.COMPLETED.ordinal }

            MyProjectWishesList(
                wishes = wishes,
                totalCount = totalCount,
                successCount = successCount,
                onItemClick = { wishId ->
                    currentWishId = wishId
                    showBottomSheet = true
                },
                modifier = modifier
            )
        }
    }

    if (showBottomSheet) {
        MyProjectBottomSheet(
            sheetState = sheetState,
            currentWishId = currentWishId,
            scope = scope,
            viewModel = viewModel
        ) {
            showBottomSheet = it
        }
    }
}