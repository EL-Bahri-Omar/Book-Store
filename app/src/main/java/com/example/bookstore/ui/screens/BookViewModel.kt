package com.example.bookstore.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.BookRepository
import com.example.bookstore.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class BookUiState {
    data object Loading : BookUiState()
    data class Success(val books: List<Book>) : BookUiState()
    data class Error(val message: String) : BookUiState()
}

data class CartItem(
    val book: Book,
    val quantity: Int
)

data class BookUiModel(
    val books: List<Book> = emptyList(),
    val featuredBooks: List<Book> = emptyList(),
    val wishlistBooks: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val cartItems: List<CartItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class BookViewModel(val repository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiModel())
    val uiState: StateFlow<BookUiModel> = _uiState.asStateFlow()

    private val _bookUiState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val bookUiState: StateFlow<BookUiState> = _bookUiState.asStateFlow()

    init {
        loadBooks()
        loadWishlist()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _bookUiState.value = BookUiState.Loading
            try {
                repository.fetchAllBooks().onSuccess { books ->
                    _bookUiState.value = BookUiState.Success(books)

                    // Separate featured books (first 5)
                    val featured = books.take(5)

                    _uiState.update {
                        it.copy(
                            books = books,
                            featuredBooks = featured
                        )
                    }
                }.onFailure { error ->
                    _bookUiState.value = BookUiState.Error(error.message ?: "Failed to load books")
                    // Fallback to local database
                    repository.getAllBooks().collect { localBooks ->
                        _uiState.update {
                            it.copy(
                                books = localBooks,
                                featuredBooks = localBooks.take(5)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _bookUiState.value = BookUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    private fun loadWishlist() {
        viewModelScope.launch {
            repository.getWishlistBooks().collect { wishlist ->
                _uiState.update { it.copy(wishlistBooks = wishlist) }
            }
        }
    }

    // Toggle wishlist for a book
    fun toggleWishlistForBook(book: Book) {
        viewModelScope.launch {
            repository.toggleWishlist(book)

            // Update the UI state immediately
            _uiState.update { currentState ->
                val updatedBooks = currentState.books.map {
                    if (it.id == book.id) {
                        it.copy(isInWishlist = !it.isInWishlist)
                    } else {
                        it
                    }
                }

                val updatedFeaturedBooks = currentState.featuredBooks.map {
                    if (it.id == book.id) {
                        it.copy(isInWishlist = !it.isInWishlist)
                    } else {
                        it
                    }
                }

                currentState.copy(
                    books = updatedBooks,
                    featuredBooks = updatedFeaturedBooks
                )
            }

            // Refresh wishlist data
            loadWishlist()
        }
    }

    fun searchBooks(query: String) {
        // Update search query immediately for UI
        _uiState.update { it.copy(searchQuery = query, isLoading = query.isNotEmpty()) }

        viewModelScope.launch {
            try {
                if (query.isNotEmpty()) {
                    // Use Flow to get books and filter them
                    repository.getAllBooks().collect { allBooks ->
                        val filteredBooks = allBooks.filter { book ->
                            book.title.contains(query, ignoreCase = true)
                        }

                        _uiState.update {
                            it.copy(
                                books = filteredBooks,
                                isLoading = false
                            )
                        }
                    }
                } else {
                    // If query is empty, load all books
                    loadBooks()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Search failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            if (category == "All") {
                loadBooks()
            } else {
                repository.fetchBooksByCategoryFromOpenLibrary(category).onSuccess { books ->
                    _uiState.update {
                        it.copy(
                            books = books,
                            isLoading = false
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = "Failed to load category: ${error.message}",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun selectBook(book: Book) {
        _uiState.update { it.copy(selectedBook = book) }
    }

    fun addToCart(book: Book) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val existingItem = currentCart.find { it.book.id == book.id }

        if (existingItem != null) {
            currentCart[currentCart.indexOf(existingItem)] = existingItem.copy(
                quantity = existingItem.quantity + 1
            )
        } else {
            currentCart.add(CartItem(book, 1))
        }

        _uiState.update { it.copy(cartItems = currentCart) }

        // Show a success message
        println("Added ${book.title} to cart")
    }

    fun removeFromCart(bookId: String) {
        _uiState.update { state ->
            state.copy(
                cartItems = state.cartItems.filter { it.book.id != bookId }
            )
        }
    }

    fun updateCartQuantity(bookId: String, quantity: Int) {
        val currentCart = _uiState.value.cartItems.toMutableList()
        val existingItem = currentCart.find { it.book.id == bookId }

        if (existingItem != null) {
            if (quantity <= 0) {
                removeFromCart(bookId)
            } else {
                currentCart[currentCart.indexOf(existingItem)] = existingItem.copy(
                    quantity = quantity
                )
                _uiState.update { it.copy(cartItems = currentCart) }
            }
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartItems = emptyList()) }
    }

    fun calculateCartTotal(): Double {
        return _uiState.value.cartItems.sumOf { it.book.price * it.quantity }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}