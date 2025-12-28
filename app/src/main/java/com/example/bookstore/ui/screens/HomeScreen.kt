package com.example.bookstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.bookstore.model.Book

@Composable
fun HomeScreen(
    viewModel: BookViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookUiState by viewModel.bookUiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Book Store",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { navController.navigate("profile") }) {
                    Icon(Icons.Default.Person, contentDescription = "Profile")
                }

                Box {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }

                    // Show number of items if cart has items
                    if (uiState.cartItems.isNotEmpty()) {
                        androidx.compose.material3.Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp),
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text(
                                text = uiState.cartItems.size.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.searchBooks(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search books by title...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Show loading
        when (bookUiState) {
            is BookUiState.Loading -> {
                if (uiState.searchQuery.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading books...")
                        }
                    }
                }
            }

            is BookUiState.Success -> {
                // Only show featured books when not searching
                if (uiState.searchQuery.isEmpty() && uiState.featuredBooks.isNotEmpty()) {
                    // Featured Books
                    Text(
                        text = "Featured Books",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.featuredBooks) { book ->
                            FeaturedBookCard(
                                book = book,
                                viewModel = viewModel,
                                onClick = {
                                    viewModel.selectBook(book)
                                    navController.navigate("book/${book.id}")
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Categories
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val categories = listOf(
                        "All", "Fiction", "Science", "History",
                        "Biography", "Fantasy", "Romance", "Mystery"
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = uiState.selectedCategory == category,
                                onClick = { viewModel.selectCategory(category) },
                                label = { Text(category) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // All Books Section or Search Results
                when {
                    uiState.isLoading -> {
                        // Show loading for search results
                        if (uiState.searchQuery.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Searching...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                CircularProgressIndicator()
                            }
                        }
                    }

                    uiState.books.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when {
                                uiState.searchQuery.isNotEmpty() -> {
                                    // No search results
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "No results",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No books found for '${uiState.searchQuery}'")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Try a different search term",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                uiState.featuredBooks.isEmpty() -> {
                                    // No books available at all
                                    Icon(
                                        Icons.Default.Book,
                                        contentDescription = "No books",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No books available")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Please try again later",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        // Show books
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) "Search Results" else "All Books",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        uiState.books.forEach { book ->
                            BookListItem(
                                book = book,
                                viewModel = viewModel,
                                onAddToCart = { viewModel.addToCart(book) },
                                onClick = {
                                    viewModel.selectBook(book)
                                    navController.navigate("book/${book.id}")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            is BookUiState.Error -> {
                // Show error state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Failed to load books",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (bookUiState as BookUiState.Error).message ?: "Unknown error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturedBookCard(book: Book, viewModel: BookViewModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = book.coverImageUrl,
                        error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
                    ),
                    contentDescription = book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Wishlist button
                IconButton(
                    onClick = { viewModel.toggleWishlistForBook(book) },
                    modifier = Modifier
                        .size(36.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        if (book.isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (book.isInWishlist) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp),
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 8.dp),
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Text(
                text = book.formattedPrice(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun BookListItem(
    book: Book,
    viewModel: BookViewModel,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Book Cover
            Image(
                painter = rememberAsyncImagePainter(
                    model = book.coverImageUrl,
                    error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
                ),
                contentDescription = book.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Book Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    fontWeight = FontWeight.SemiBold,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.formattedPrice(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        // Wishlist button
                        IconButton(
                            onClick = {
                                viewModel.toggleWishlistForBook(book)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                if (book.isInWishlist) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (book.isInWishlist) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Add to cart button
                        IconButton(
                            onClick = onAddToCart,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.AddShoppingCart,
                                contentDescription = "Add to cart",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}