package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookstore.ui.components.BookstoreBottomNavigation

@Composable
fun MainScreen(
    bookViewModel: BookViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination

    // Get the parent route to determine if bottom nav should show
    val currentRoute = currentDestination?.route ?: "home"
    val isBottomNavRoute = when {
        currentRoute.startsWith("home") -> true
        currentRoute.startsWith("wishlist") -> true
        currentRoute.startsWith("orders") -> true
        currentRoute.startsWith("profile") -> true
        else -> false
    }

    // Track the actual bottom nav route separately
    val bottomNavRoute = when {
        currentRoute.startsWith("home") -> "home"
        currentRoute.startsWith("wishlist") -> "wishlist"
        currentRoute.startsWith("orders") -> "orders"
        currentRoute.startsWith("profile") -> "profile"
        else -> "home"
    }

    Scaffold(
        bottomBar = {
            if (isBottomNavRoute) {
                BookstoreBottomNavigation(
                    currentRoute = bottomNavRoute,
                    onItemSelected = { item ->
                        if (item.route != bottomNavRoute) {
                            navController.navigate(item.route) {
                                // Clear the back stack to prevent confusion
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Prevent multiple instances
                                launchSingleTop = true
                                // Restore state when reselecting
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    viewModel = bookViewModel,
                    navController = navController
                )
            }

            composable("wishlist") {
                WishlistScreen(
                    viewModel = bookViewModel,
                    navController = navController
                )
            }

            composable("orders") {
                OrderScreen(
                    viewModel = bookViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable("profile") {
                ProfileScreen(
                    authViewModel = authViewModel,
                    viewModel = bookViewModel,
                    navController = navController
                )
            }

            composable("book/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                val book = remember(bookId) {
                    // Get book from ViewModel
                    bookViewModel.uiState.value.books.find { it.id == bookId }
                }
                BookDetailScreen(
                    book = book,
                    viewModel = bookViewModel,
                    navController = navController
                )
            }

            composable("cart") {
                CartScreen(
                    viewModel = bookViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable("auth") {
                AuthScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable("checkout") {
                CheckoutScreen(
                    viewModel = bookViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }
        }
    }
}