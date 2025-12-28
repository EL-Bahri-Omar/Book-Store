package com.example.bookstore.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Wishlist : BottomNavItem(
        route = "wishlist",
        title = "Wishlist",
        icon = Icons.Default.Favorite
    )

    data object Orders : BottomNavItem(
        route = "orders",
        title = "Orders",
        icon = Icons.Default.ShoppingBag
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
}

@Composable
fun BookstoreBottomNavigation(
    currentRoute: String,
    onItemSelected: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Wishlist,
        BottomNavItem.Orders,
        BottomNavItem.Profile
    )

    androidx.compose.material3.NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
            )
        }
    }
}