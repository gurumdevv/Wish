package com.gurumlab.wish.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.gson.Gson
import com.gurumlab.wish.data.model.Wish
import com.gurumlab.wish.ui.detail.DetailRoute
import com.gurumlab.wish.ui.home.HomeRoute
import com.gurumlab.wish.ui.message.Message
import com.gurumlab.wish.ui.post.PostDescriptionRoute
import com.gurumlab.wish.ui.post.PostExaminationRoute
import com.gurumlab.wish.ui.post.PostFeaturesRoute
import com.gurumlab.wish.ui.post.PostStartRoute
import com.gurumlab.wish.ui.post.PostViewModel
import com.gurumlab.wish.ui.progressForDeveloper.ProgressForDeveloperRoute
import com.gurumlab.wish.ui.projectSubmit.ProjectSubmitRoute
import com.gurumlab.wish.ui.settings.AccountSettingScreen
import com.gurumlab.wish.ui.settings.ApproachingProjectSettingRoute
import com.gurumlab.wish.ui.settings.MyProjectSettingRoute
import com.gurumlab.wish.ui.settings.SettingsRoute
import com.gurumlab.wish.ui.settings.TermsAndConditionRoute
import com.gurumlab.wish.ui.wishes.WishesRoute

enum class WishScreen {
    HOME,
    WISHES,
    POST,
    MESSAGE,
    SETTING,
    DETAIL,
    PROGRESS_FOR_DEVELOPER,
    PROJECT_SUBMIT,
    SETTINGS,
    ACCOUNT_SETTING,
    MY_PROJECT_SETTING,
    APPROACHING_PROJECT_SETTING,
    TERMS_AND_CONDITION,
    POST_START,
    POST_DESCRIPTION,
    POST_FEATURES,
    POST_EXAMINATION
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
        composable(route = WishScreen.MESSAGE.name) {
            Message()
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
        navigation(startDestination = WishScreen.SETTINGS.name, route = WishScreen.SETTING.name) {
            composable(route = WishScreen.SETTINGS.name) {
                SettingsRoute(
                    onAccountSetting = {
                        navController.navigate(WishScreen.ACCOUNT_SETTING.name)
                    },
                    onMyProjectSetting = {
                        navController.navigate(WishScreen.MY_PROJECT_SETTING.name)
                    },
                    onApproachingProjectSetting = {
                        navController.navigate(WishScreen.APPROACHING_PROJECT_SETTING.name)
                    },
                    onTermsAndCondition = {
                        navController.navigate(WishScreen.TERMS_AND_CONDITION.name)
                    }
                )
            }
            composable(
                route = WishScreen.ACCOUNT_SETTING.name
            ) {
                AccountSettingScreen()
            }
            composable(
                route = WishScreen.MY_PROJECT_SETTING.name
            ) {
                MyProjectSettingRoute()
            }
            composable(
                route = WishScreen.APPROACHING_PROJECT_SETTING.name
            ) {
                ApproachingProjectSettingRoute()
            }
            composable(
                route = WishScreen.TERMS_AND_CONDITION.name
            ) {
                TermsAndConditionRoute()
            }
        }
        navigation(startDestination = WishScreen.POST_START.name, route = WishScreen.POST.name) {
            composable(route = WishScreen.POST_START.name) {
                val viewModel = it.sharedViewModel<PostViewModel>(navController = navController)
                PostStartRoute(viewModel) {
                    navController.navigate(WishScreen.POST_DESCRIPTION.name)
                }
            }
            composable(
                route = WishScreen.POST_DESCRIPTION.name
            ) {
                val viewModel = it.sharedViewModel<PostViewModel>(navController = navController)
                PostDescriptionRoute(viewModel) {
                    navController.navigate(WishScreen.POST_FEATURES.name)
                }
            }
            composable(
                route = WishScreen.POST_FEATURES.name
            ) {
                val viewModel = it.sharedViewModel<PostViewModel>(navController = navController)
                PostFeaturesRoute(viewModel) {
                    navController.navigate(WishScreen.POST_EXAMINATION.name)
                }
            }
            composable(
                route = WishScreen.POST_EXAMINATION.name
            ) {
                val viewModel = it.sharedViewModel<PostViewModel>(navController = navController)
                PostExaminationRoute(viewModel) {
                    navController.navigate(WishScreen.WISHES.name) {
                        popUpTo(WishScreen.POST.name) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavHostController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}