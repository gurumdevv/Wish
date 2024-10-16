package com.gurumlab.wish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.gurumlab.wish.navigation.WishNavHost
import com.gurumlab.wish.navigation.WishScreen
import com.gurumlab.wish.ui.theme.WishTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        lifecycleScope.launch {
            viewModel.uid.collect {
                splashScreen.setKeepOnScreenCondition { it == null }
            }
        }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false

        setContent {
            WishTheme {
                viewModel.uid.collectAsStateWithLifecycle().value?.let { uid ->
                    val startDestination =
                        if (uid.isBlank()) WishScreen.LOGIN.name else WishScreen.HOME.name
                    WishNavHost(startDestination = startDestination)
                }
            }
        }
    }
}