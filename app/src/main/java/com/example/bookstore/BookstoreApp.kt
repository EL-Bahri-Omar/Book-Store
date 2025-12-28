package com.example.bookstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookstore.data.AppContainer
import com.example.bookstore.ui.screens.MainScreen
import com.example.bookstore.ui.theme.BookstoreTheme

@Composable
fun BookstoreApp() {
    BookstoreTheme {
        val context = LocalContext.current

        // Create AppContainer with context
        val appContainer = remember { AppContainer(context) }

        // Get ViewModel factories
        val bookViewModelFactory = appContainer.provideBookViewModelFactory()
        val authViewModelFactory = appContainer.provideAuthViewModelFactory()

        // Create ViewModels
        val bookViewModel: com.example.bookstore.ui.screens.BookViewModel = viewModel(factory = bookViewModelFactory)
        val authViewModel: com.example.bookstore.ui.screens.AuthViewModel = viewModel(factory = authViewModelFactory)

        // Use MainScreen with bottom navigation
        MainScreen(
            bookViewModel = bookViewModel,
            authViewModel = authViewModel
        )
    }
}