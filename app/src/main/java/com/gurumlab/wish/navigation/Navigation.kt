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
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.detail.DetailRoute
import com.gurumlab.wish.ui.home.HomeRoute
import com.gurumlab.wish.ui.login.LoginRoute
import com.gurumlab.wish.ui.login.PolicyAgreementRoute
import com.gurumlab.wish.ui.message.Message
import com.gurumlab.wish.ui.post.PostDescriptionRoute
import com.gurumlab.wish.ui.post.PostExaminationRoute
import com.gurumlab.wish.ui.post.PostFeaturesRoute
import com.gurumlab.wish.ui.post.PostStartRoute
import com.gurumlab.wish.ui.post.PostViewModel
import com.gurumlab.wish.ui.progressForDeveloper.ProgressForDeveloperRoute
import com.gurumlab.wish.ui.projectSubmit.ProjectSubmitRoute
import com.gurumlab.wish.ui.settings.AccountSettingRoute
import com.gurumlab.wish.ui.settings.ApproachingProjectSettingRoute
import com.gurumlab.wish.ui.settings.MyProjectSettingRoute
import com.gurumlab.wish.ui.settings.SettingsRoute
import com.gurumlab.wish.ui.settings.SettingsViewModel
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
    POST_EXAMINATION,
    LOGIN,
    START,
    POLICY_AGREEMENT
}

@Composable
fun WishNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = WishScreen.HOME.name) {
            HomeRoute { wishId ->
                navController.navigate(WishScreen.DETAIL.name + "/${wishId}")
            }
        }
        composable(route = WishScreen.WISHES.name) {
            WishesRoute { wishId ->
                navController.navigate(WishScreen.DETAIL.name + "/${wishId}")
            }
        }
        composable(route = WishScreen.MESSAGE.name) {
            Message()
        }
        composable(
            route = WishScreen.DETAIL.name + "/{wishId}"
        ) { backStackEntry ->
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            DetailRoute(
                wishId = wishId,
                onProgressScreen = { minimizedWishObject, wishIdString ->
                    navController.navigate(
                        WishScreen.PROGRESS_FOR_DEVELOPER.name
                                + "/${Gson().toJson(minimizedWishObject)}"
                                + "/${wishIdString}"
                    )
                },
                onMessageScreen = {
                    //TODO("Go to Message")
                }
            )
        }
        composable(
            route = WishScreen.PROGRESS_FOR_DEVELOPER.name + "/{minimizedWish}" + "/{wishId}"
        ) { backStackEntry ->
            val minimizedWishJson = backStackEntry.arguments?.getString("minimizedWish")
            val minimizedWish = Gson().fromJson(minimizedWishJson, MinimizedWish::class.java)
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            ProgressForDeveloperRoute(
                minimizedWish = minimizedWish,
                wishId = wishId,
                onSubmitScreen = { minimizedWishObject, wishIdString ->
                    navController.navigate(
                        WishScreen.PROJECT_SUBMIT.name + "/${Gson().toJson(minimizedWishObject)}" + "/${wishIdString}"
                    )
                },
                onMessageScreen = {
                    //TODO("Go to Message")
                }
            )
        }
        composable(
            route = WishScreen.PROJECT_SUBMIT.name + "/{minimizedWish}" + "/{wishId}"
        ) { backStackEntry ->
            val minimizedWishJson = backStackEntry.arguments?.getString("minimizedWish")
            val minimizedWish = Gson().fromJson(minimizedWishJson, MinimizedWish::class.java)
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            ProjectSubmitRoute(minimizedWish, wishId) {
                navController.navigate(WishScreen.WISHES.name) {
                    popUpTo(WishScreen.DETAIL.name + "/{minimizedWish}/{wishId}") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
        navigation(startDestination = WishScreen.SETTINGS.name, route = WishScreen.SETTING.name) {
            composable(route = WishScreen.SETTINGS.name) {
                val viewModel = it.sharedViewModel<SettingsViewModel>(navController = navController)
                SettingsRoute(
                    viewModel = viewModel,
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
                val viewModel = it.sharedViewModel<SettingsViewModel>(navController = navController)
                AccountSettingRoute(viewModel) {
                    navController.navigate(WishScreen.LOGIN.name) {
                        popUpTo(WishScreen.SETTING.name) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
            composable(
                route = WishScreen.MY_PROJECT_SETTING.name
            ) {
                val viewModel = it.sharedViewModel<SettingsViewModel>(navController = navController)
                MyProjectSettingRoute(viewModel)
            }
            composable(
                route = WishScreen.APPROACHING_PROJECT_SETTING.name
            ) {
                val viewModel = it.sharedViewModel<SettingsViewModel>(navController = navController)
                ApproachingProjectSettingRoute(viewModel)
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
        navigation(startDestination = WishScreen.START.name, route = WishScreen.LOGIN.name) {
            composable(route = WishScreen.START.name) {
                LoginRoute {
                    navController.navigate(WishScreen.POLICY_AGREEMENT.name)
                }
            }
            composable(route = WishScreen.POLICY_AGREEMENT.name) {
                PolicyAgreementRoute {
                    navController.navigate(WishScreen.HOME.name) {
                        popUpTo(WishScreen.LOGIN.name) {
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