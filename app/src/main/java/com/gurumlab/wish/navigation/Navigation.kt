package com.gurumlab.wish.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.detail.DetailRoute
import com.gurumlab.wish.ui.home.HomeRoute
import com.gurumlab.wish.ui.message.Message
import com.gurumlab.wish.ui.post.Post
import com.gurumlab.wish.ui.progressForDeveloper.ProgressForDeveloperRoute
import com.gurumlab.wish.ui.projectSubmit.ProjectSubmitRoute
import com.gurumlab.wish.ui.settings.SettingsRoute
import com.gurumlab.wish.ui.wishes.WishesRoute

enum class WishScreen {
    HOME,
    WISHES,
    POST,
    MESSAGE,
    SETTINGS,
    DETAIL,
    PROGRESS_FOR_DEVELOPER,
    PROJECT_SUBMIT
}

@Composable
fun WishNavHost(
    navController: NavHostController
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
            SettingsRoute()
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
            route = WishScreen.PROGRESS_FOR_DEVELOPER.name + "/{wish}" + "/{wishId}"
        ) { backStackEntry ->
            val wishJson = backStackEntry.arguments?.getString("wish")
            val wish = Gson().fromJson(wishJson, Wish::class.java)
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            ProgressForDeveloperRoute(wish, wishId) { wishObject, wishIdString ->
                navController.navigate(
                    WishScreen.PROJECT_SUBMIT.name + "/${Gson().toJson(wishObject)}" + "/${wishIdString}"
                )
            }
        }
        composable(
            route = WishScreen.PROJECT_SUBMIT.name + "/{wish}" + "/{wishId}"
        ) { backStackEntry ->
            val wishJson = backStackEntry.arguments?.getString("wish")
            val wish = Gson().fromJson(wishJson, Wish::class.java)
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            ProjectSubmitRoute(wish, wishId) {
                navController.navigate(WishScreen.WISHES.name) {
                    popUpTo(WishScreen.DETAIL.name + "/{wish}/{wishId}") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}