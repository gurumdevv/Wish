package com.gurumlab.wish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gurumlab.wish.navigation.BottomNavigationBar
import com.gurumlab.wish.navigation.NavigationActions
import com.gurumlab.wish.navigation.WishNavHost
import com.gurumlab.wish.navigation.WishScreen
import com.gurumlab.wish.ui.theme.WishTheme
import com.gurumlab.wish.ui.util.ScaffoldTopAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        lifecycleScope.launch {
            viewModel.getUid()
            splashScreen.setKeepOnScreenCondition { viewModel.uid.value == null }
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        setContent {
            WishTheme {
                val uidState by viewModel.uid.collectAsStateWithLifecycle()
                uidState?.let { MainScreen(uid = it) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uid: String
) {
    val navController = rememberNavController()
    val navigationActions = remember(navController) { NavigationActions(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: WishScreen.HOME.name
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val specificScreens = listOf(
        "DETAIL",
        "PROGRESS_FOR_DEVELOPER",
        "PROJECT_SUBMIT",
        "ACCOUNT_SETTING",
        "MY_PROJECT_SETTING",
        "APPROACHING_PROJECT_SETTING",
        "TERMS_AND_CONDITION",
        "POST_START",
        "POST_DESCRIPTION",
        "POST_FEATURES",
        "POST_EXAMINATION",
        "START",
        "POLICY_AGREEMENT"
    )

    val isSpecificScreen = selectedDestination.let {
        specificScreens.contains(selectedDestination.split("/").first())
    }

    Scaffold(
        topBar = {
            if (isSpecificScreen && (selectedDestination != WishScreen.START.name)) {
                ScaffoldTopAppBar(
                    scrollBehavior = scrollBehavior,
                    onNavIconPressed = { navController.navigateUp() }
                )
            }
        },
        bottomBar = {
            if (!isSpecificScreen) {
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
            val startDestination =
                if (uid.isBlank()) WishScreen.LOGIN.name else WishScreen.HOME.name
            WishNavHost(navController = navController, startDestination = startDestination)
        }
    }
}