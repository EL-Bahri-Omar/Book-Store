package com.example.bookstore.data

import android.content.Context
import com.example.bookstore.database.BookDatabase
import com.example.bookstore.network.RetrofitClient
import com.example.bookstore.ui.screens.AuthViewModelFactory
import com.example.bookstore.ui.screens.BookViewModelFactory

class AppContainer(private val context: Context) {

    private val database by lazy { BookDatabase.getDatabase(context) }
    private val apiService by lazy { RetrofitClient.instance }

    private val repository by lazy {
        BookRepository(
            bookDao = database.bookDao(),
            userDao = database.userDao(),
            orderDao = database.orderDao(),
            apiService = apiService
        )
    }

    fun provideBookViewModelFactory(): BookViewModelFactory {
        return BookViewModelFactory(repository)
    }

    fun provideAuthViewModelFactory(): AuthViewModelFactory {
        return AuthViewModelFactory(repository)
    }
}