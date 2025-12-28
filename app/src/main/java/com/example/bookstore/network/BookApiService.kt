package com.example.bookstore.network

import com.example.bookstore.model.Book
import com.example.bookstore.network.model.OpenLibrarySearchResponse
import com.example.bookstore.network.model.OpenLibraryWorkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

interface BookApiService {

    // Open Library API endpoints
    @GET("search.json")
    suspend fun searchBooksOpenLibrary(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse

    @GET("search.json")
    suspend fun getBooksBySubjectOpenLibrary(
        @Query("subject") subject: String,
        @Query("limit") limit: Int = 20
    ): OpenLibrarySearchResponse
}