package com.gurumlab.wish.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.detail.DetailRoute
import com.gurumlab.wish.ui.home.HomeRoute
import com.gurumlab.wish.ui.message.Message
import com.gurumlab.wish.ui.post.Post
import com.gurumlab.wish.ui.progressForDeveloper.ProgressForDeveloperRoute
import com.gurumlab.wish.ui.settings.Settings
import com.gurumlab.wish.ui.wishes.WishesRoute

enum class WishScreen {
    HOME,
    WISHES,
    POST,
    MESSAGE,
    SETTINGS,
    DETAIL,
    PROGRESS_FOR_DEVELOPER
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
            HomeRoute { wish, wishId ->
                navController.navigate(WishScreen.DETAIL.name + "/${Gson().toJson(wish)}" + "/${wishId}")
            }
        }
        composable(route = WishScreen.WISHES.name) {
            WishesRoute { wish, wishId ->
                navController.navigate(WishScreen.DETAIL.name + "/${Gson().toJson(wish)}" + "/${wishId}")
            }
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
        composable(
            route = WishScreen.DETAIL.name + "/{wish}" + "/{wishId}"
        ) { backStackEntry ->
            val wishJson = backStackEntry.arguments?.getString("wish")
            val wish = Gson().fromJson(wishJson, Wish::class.java)
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            DetailRoute(
                wish,
                wishId,
                onProgressScreen = { wishObject, wishIdString ->
                    navController.navigate(
                        WishScreen.PROGRESS_FOR_DEVELOPER.name + "/${Gson().toJson(wishObject)}" + "/${wishIdString}"
                    )
                },
                onMessageScreen = {} //TODO("메세지로 이동")
            )
        }
        composable(
            route = WishScreen.PROGRESS_FOR_DEVELOPER.name + "/{wish}"
        ) { backStackEntry ->
            val wishJson = backStackEntry.arguments?.getString("wish")
            val wish = Gson().fromJson(wishJson, Wish::class.java)
            ProgressForDeveloperRoute(wish)
        }
    }
}