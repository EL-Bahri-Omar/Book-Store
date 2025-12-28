package com.example.bookstore.database

import androidx.room.*
import com.example.bookstore.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<Book>)

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBook(bookId: String): Book?

    @Query("SELECT * FROM books WHERE category = :category")
    fun getBooksByCategory(category: String): Flow<List<Book>>

    // FIXED: Search for exact title matches (case-insensitive)
    @Query("SELECT * FROM books WHERE LOWER(title) LIKE '%' || LOWER(:query) || '%' ORDER BY title ASC")
    fun searchBooks(query: String): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE isInWishlist = 1 ORDER BY title ASC")
    fun getWishlistBooks(): Flow<List<Book>>

    @Query("UPDATE books SET isInWishlist = :inWishlist WHERE id = :bookId")
    suspend fun updateWishlistStatus(bookId: String, inWishlist: Boolean)

    @Query("DELETE FROM books")
    suspend fun deleteAll()
}