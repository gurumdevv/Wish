package com.gurumlab.wish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gurumlab.wish.navigation.BottomNavigationBar
import com.gurumlab.wish.navigation.NavigationActions
import com.gurumlab.wish.navigation.WishNavHost
import com.gurumlab.wish.navigation.WishScreen
import com.gurumlab.wish.ui.theme.WishTheme
import com.gurumlab.wish.ui.util.ScaffoldTopAppBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        setContent {
            WishTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) { NavigationActions(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: WishScreen.HOME.name
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    val showTopBarScreen = listOf(WishScreen.DETAIL.name)

    Scaffold(
        topBar = {
            showTopBarScreen.forEach { screen ->
                if (selectedDestination.startsWith(screen)) {
                    ScaffoldTopAppBar(
                        scrollBehavior = scrollBehavior,
                        onNavIconPressed = { navController.navigateUp() }
                    )
                }
            }
        },
        bottomBar = {
            if (!selectedDestination.startsWith(WishScreen.DETAIL.name)) {
                BottomNavigationBar(
                    currentDestination = selectedDestination,
                    navigateToDestination = navigationActions::navigateTo
                )
            }
        }) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            WishNavHost(navController = navController)
        }
    }
}