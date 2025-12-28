package com.example.bookstore.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // using Open Library API as our main data source
    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Open Library API service
    val openLibraryService: BookApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_LIBRARY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(BookApiService::class.java)
    }

    // Default instance uses Open Library
    val instance: BookApiService = openLibraryService
}