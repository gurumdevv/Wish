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
import com.gurumlab.wish.data.model.ChatRoom
import com.gurumlab.wish.data.model.MinimizedWish
import com.gurumlab.wish.ui.detail.DetailRoute
import com.gurumlab.wish.ui.home.HomeRoute
import com.gurumlab.wish.ui.login.LoginRoute
import com.gurumlab.wish.ui.login.PolicyAgreementRoute
import com.gurumlab.wish.ui.message.ChatRoomRoute
import com.gurumlab.wish.ui.message.ChatsRoute
import com.gurumlab.wish.ui.message.DonationRoute
import com.gurumlab.wish.ui.message.RepositoryRedirectRoute
import com.gurumlab.wish.ui.message.SubmissionViewModel
import com.gurumlab.wish.ui.message.moveToChatRoom
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
import java.net.URLDecoder
import java.net.URLEncoder

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
    POLICY_AGREEMENT,
    CHATS,
    CHAT_ROOM,
    DONATION,
    REPOSITORY_REDIRECT
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
        navigation(startDestination = WishScreen.CHATS.name, route = WishScreen.MESSAGE.name) {
            composable(route = WishScreen.CHATS.name) {
                ChatsRoute { chatRoom, name, imageUrl ->
                    navController.navigate(
                        WishScreen.CHAT_ROOM.name
                                + "/${Gson().toJson(chatRoom)}"
                                + "/${name}"
                                + "/" + URLEncoder.encode(imageUrl, "UTF-8")
                    )
                }
            }
            composable(
                route = WishScreen.CHAT_ROOM.name + "/{chatRoom}" + "/{name}" + "/{imageUrl}"
            ) { backStackEntry ->
                val chatRoomJson = backStackEntry.arguments?.getString("chatRoom")
                val chatRoom = Gson().fromJson(chatRoomJson, ChatRoom::class.java)
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val imageUrl =
                    URLDecoder.decode(backStackEntry.arguments?.getString("imageUrl"), "UTF-8")
                ChatRoomRoute(
                    chatRoom = chatRoom,
                    otherUserName = name,
                    otherUserImageUrl = imageUrl,
                    onRepository = { completedWishId ->
                        navController.navigate(
                            WishScreen.REPOSITORY_REDIRECT.name + "/${completedWishId}"
                        )
                    },
                    onDonation = { completedWishId ->
                        navController.navigate(
                            WishScreen.DONATION.name + "/${completedWishId}"
                        )
                    }
                )
            }
            composable(
                route = WishScreen.DONATION.name + "/{completedWishId}"
            ) { backStackEntry ->
                val viewModel =
                    backStackEntry.sharedViewModel<SubmissionViewModel>(navController = navController)
                val completedWishId = backStackEntry.arguments?.getString("completedWishId") ?: ""
                DonationRoute(viewModel, completedWishId) {
                    navController.navigate(WishScreen.WISHES.name) {
                        popUpTo(WishScreen.MESSAGE.name) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
            composable(
                route = WishScreen.REPOSITORY_REDIRECT.name + "/{completedWishId}"
            ) { backStackEntry ->
                val viewModel =
                    backStackEntry.sharedViewModel<SubmissionViewModel>(navController = navController)
                val completedWishId = backStackEntry.arguments?.getString("completedWishId") ?: ""
                RepositoryRedirectRoute(viewModel, completedWishId) {
                    navController.navigateUp()
                }
            }
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
                    moveToChatRoom(it.posterId) { chatRoom, name, imageUrl ->
                        navController.navigate(
                            WishScreen.CHAT_ROOM.name
                                    + "/${Gson().toJson(chatRoom)}"
                                    + "/${name}"
                                    + "/" + URLEncoder.encode(imageUrl, "UTF-8")
                        )
                    }
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
                    moveToChatRoom(it.posterId) { chatRoom, name, imageUrl ->
                        navController.navigate(
                            WishScreen.CHAT_ROOM.name
                                    + "/${Gson().toJson(chatRoom)}"
                                    + "/${name}"
                                    + "/" + URLEncoder.encode(imageUrl, "UTF-8")
                        )
                    }
                }
            )
        }
        composable(
            route = WishScreen.PROJECT_SUBMIT.name + "/{minimizedWish}" + "/{wishId}"
        ) { backStackEntry ->
            val minimizedWishJson = backStackEntry.arguments?.getString("minimizedWish")
            val minimizedWish = Gson().fromJson(minimizedWishJson, MinimizedWish::class.java)
            val wishId = backStackEntry.arguments?.getString("wishId") ?: ""
            ProjectSubmitRoute(wishId, minimizedWish) {
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
                        popUpTo(WishScreen.HOME.name) {
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