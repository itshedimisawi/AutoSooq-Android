package com.example.googlelogindemo.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.*

sealed class BottomNavigationScreens(
    var route: String,
    var icon: ImageVector,
) {
    object HomeBottomNavigationScreens : BottomNavigationScreens(
        Screens.HomeScreen.route,
        FeatherIcons.Home
    )
    object FavoritesBottomNavigationScreens : BottomNavigationScreens(
        Screens.FavoritesScreen.route,
        FeatherIcons.Heart
    )
    object SellBottomNavigationScreens : BottomNavigationScreens(
        Screens.SellScreen.route,
        FeatherIcons.PlusSquare
    )
    object MyPostsBottomNavigationScreens : BottomNavigationScreens(
        Screens.MyPostsScreen.route,
        FeatherIcons.ShoppingBag
    )
    object AccountBottomNavigationScreens : BottomNavigationScreens(
        Screens.AccountScreen.route,
        FeatherIcons.User
    )
}