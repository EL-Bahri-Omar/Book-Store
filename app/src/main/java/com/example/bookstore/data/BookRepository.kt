package com.example.bookstore.data

import com.example.bookstore.database.BookDao
import com.example.bookstore.database.OrderDao
import com.example.bookstore.database.UserDao
import com.example.bookstore.model.Book
import com.example.bookstore.model.Order
import com.example.bookstore.model.User
import com.example.bookstore.network.BookApiService
import com.example.bookstore.network.model.OpenLibraryBook
import kotlinx.coroutines.flow.Flow
import kotlin.random.Random
import java.util.UUID

class BookRepository(
    private val bookDao: BookDao,
    private val userDao: UserDao,
    private val orderDao: OrderDao,
    private val apiService: BookApiService
) {
    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun registerUser(user: User): Long = userDao.insert(user)

    suspend fun login(email: String, password: String): User? = userDao.login(email, password)

    suspend fun getUserById(id: Int): User? = userDao.getUserById(id)

    suspend fun updateUser(user: User) { userDao.update(user) }

    suspend fun placeOrder(order: Order): Long = orderDao.insert(order)

    fun getOrdersByUser(userId: Int): Flow<List<Order>> = orderDao.getOrdersByUser(userId)

    // Wishlist operations
    fun getWishlistBooks(): Flow<List<Book>> = bookDao.getWishlistBooks()

    suspend fun toggleWishlist(book: Book) {
        val existingBook = bookDao.getBook(book.id)
        if (existingBook != null) {
            val updatedBook = existingBook.copy(isInWishlist = !existingBook.isInWishlist)
            bookDao.update(updatedBook)
        } else {
            val newBook = book.copy(isInWishlist = true)
            bookDao.insert(newBook)
        }
    }

    // Open Library API operations
    suspend fun fetchBooksFromOpenLibrary(query: String = ""): Result<List<Book>> {
        return try {
            val response = if (query.isNotEmpty()) {
                apiService.searchBooksOpenLibrary(query, limit = 20)
            } else {
                // Fetch popular books
                apiService.searchBooksOpenLibrary("fiction", limit = 20)
            }

            val books = response.docs.mapNotNull { openLibraryBook ->
                convertToBookModel(openLibraryBook)
            }

            // Cache the fetched books locally
            if (books.isNotEmpty()) {
                // Check wishlist status for existing books before inserting
                books.forEach { newBook ->
                    val existingBook = bookDao.getBook(newBook.id)
                    if (existingBook != null) {
                        // Keep existing wishlist status
                        val updatedBook = newBook.copy(isInWishlist = existingBook.isInWishlist)
                        bookDao.update(updatedBook)
                    } else {
                        bookDao.insert(newBook)
                    }
                }
            }

            Result.success(books)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun fetchBooksByCategoryFromOpenLibrary(category: String): Result<List<Book>> {
        return try {
            val response = apiService.getBooksBySubjectOpenLibrary(category, limit = 20)

            val books = response.docs.mapNotNull { openLibraryBook ->
                convertToBookModel(openLibraryBook)
            }

            if (books.isNotEmpty()) {
                // Check wishlist status for existing books
                books.forEach { newBook ->
                    val existingBook = bookDao.getBook(newBook.id)
                    if (existingBook != null) {
                        val updatedBook = newBook.copy(isInWishlist = existingBook.isInWishlist)
                        bookDao.update(updatedBook)
                    } else {
                        bookDao.insert(newBook)
                    }
                }
            }

            Result.success(books)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun convertToBookModel(openLibraryBook: OpenLibraryBook): Book? {
        return try {
            val workId = openLibraryBook.key?.replace("/works/", "") ?: UUID.randomUUID().toString()
            val random = Random.Default

            Book(
                id = workId,
                title = openLibraryBook.title ?: "Unknown Title",
                author = openLibraryBook.authorName?.joinToString(", ") ?: "Unknown Author",
                description = "A great book by ${openLibraryBook.authorName?.firstOrNull() ?: "the author"}. " +
                        "Published in ${openLibraryBook.firstPublishYear ?: "unknown year"}.",
                price = 10.0 + random.nextDouble(40.0),
                category = openLibraryBook.subjects?.firstOrNull() ?: "Fiction",
                coverImageUrl = if (openLibraryBook.coverId != null) {
                    "https://covers.openlibrary.org/b/id/${openLibraryBook.coverId}-L.jpg"
                } else {
                    "https://via.placeholder.com/300x400.png?text=No+Cover"
                },
                isbn = openLibraryBook.isbn?.firstOrNull() ?: "0000000000",
                pages = openLibraryBook.numberOfPagesMedian ?: 300,
                publisher = openLibraryBook.publisher?.firstOrNull() ?: "Unknown Publisher",
                publishedDate = openLibraryBook.firstPublishYear?.toString() ?: "Unknown",
                language = openLibraryBook.language?.firstOrNull() ?: "English",
                stock = random.nextInt(0, 51),
                rating = 3.0 + random.nextDouble(2.0),
                reviews = random.nextInt(0, 1001),
                isInWishlist = false // Default to false
            )
        } catch (e: Exception) {
            null
        }
    }

    // API operations (using Open Library as backend)
    suspend fun fetchAllBooks(): Result<List<Book>> {
        return fetchBooksFromOpenLibrary("fiction")
    }
}