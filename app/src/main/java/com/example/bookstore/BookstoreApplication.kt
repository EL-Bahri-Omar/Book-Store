package com.example.bookstore

import android.app.Application
import com.example.bookstore.database.BookDatabase
import com.example.bookstore.network.RetrofitClient

class BookstoreApplication : Application() {

    val database by lazy { BookDatabase.getDatabase(this) }
    val apiService by lazy { RetrofitClient.instance }
}