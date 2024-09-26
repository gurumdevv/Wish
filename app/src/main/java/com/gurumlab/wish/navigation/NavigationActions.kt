package com.gurumlab.wish.navigation

import androidx.navigation.NavHostController
import com.gurumlab.wish.R

data class NavigationItem(
    val route: String,
    val icon: Int,
    val enabledIcon: Int,
    val iconTextId: Int
)

val navigationItems = listOf(
    NavigationItem(
        route = WishScreen.HOME.name,
        icon = R.drawable.ic_home,
        enabledIcon = R.drawable.ic_home_enabled,
        iconTextId = R.string.home
    ),
    NavigationItem(
        route = WishScreen.WISHES.name,
        icon = R.drawable.ic_wishes,
        enabledIcon = R.drawable.ic_wishes_enabled,
        iconTextId = R.string.wishes
    ),
    NavigationItem(
        route = WishScreen.POST.name,
        icon = R.drawable.ic_post,
        enabledIcon = R.drawable.ic_post,
        iconTextId = R.string.post
    ),
    NavigationItem(
        route = WishScreen.MESSAGE.name,
        icon = R.drawable.ic_message,
        enabledIcon = R.drawable.ic_message_enabled,
        iconTextId = R.string.message
    ),
    NavigationItem(
        route = WishScreen.SETTING.name,
        icon = R.drawable.ic_settings,
        enabledIcon = R.drawable.ic_settings_enabled,
        iconTextId = R.string.settings
    ),
)

class NavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: NavigationItem) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}