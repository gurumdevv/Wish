package com.gurumlab.wish.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.gurumlab.wish.ui.theme.backgroundColor

@Composable
fun BottomNavigationBar(
    currentDestination: String,
    navigateToDestination: (NavigationItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = backgroundColor,
    ) {
        navigationItems.forEach { item ->
            val isSelected = if (currentDestination == WishScreen.SETTINGS.name) {
                WishScreen.SETTING.name == item.route
            } else {
                currentDestination == item.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { navigateToDestination(item) },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = if (isSelected) item.enabledIcon else item.icon
                        ),
                        contentDescription = stringResource(id = item.iconTextId),
                        tint = Color.Unspecified
                    )
                },
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.Transparent,
                    selectedTextColor = Color.Unspecified,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = Color.Unspecified,
                    unselectedTextColor = Color.Unspecified,
                    disabledIconColor = Color.Unspecified,
                    disabledTextColor = Color.Unspecified
                )
            )
        }
    }
}