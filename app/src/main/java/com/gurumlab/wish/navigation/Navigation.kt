package com.gurumlab.wish.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gurumlab.wish.ui.home.HomeRoute
import com.gurumlab.wish.ui.message.Message
import com.gurumlab.wish.ui.post.Post
import com.gurumlab.wish.ui.settings.Settings
import com.gurumlab.wish.ui.wishes.Wishes

enum class WishScreen {
    HOME,
    WISHES,
    POST,
    MESSAGE,
    SETTINGS
}

@Composable
fun WishNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = WishScreen.HOME.name
    ) {
        composable(route = WishScreen.HOME.name) {
            HomeRoute()
        }
        composable(route = WishScreen.WISHES.name) {
            Wishes()
        }
        composable(route = WishScreen.POST.name) {
            Post()
        }
        composable(route = WishScreen.MESSAGE.name) {
            Message()
        }
        composable(route = WishScreen.SETTINGS.name) {
            Settings()
        }
    }
}