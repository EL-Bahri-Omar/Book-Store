package com.example.bookstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.bookstore.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: BookViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    "My Wishlist",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (uiState.wishlistBooks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Empty Wishlist",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your wishlist is empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add books you like to your wishlist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("home") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Browse Books")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.wishlistBooks) { book ->
                    WishlistBookCard(
                        book = book,
                        viewModel = viewModel,
                        onClick = {
                            viewModel.selectBook(book)
                            navController.navigate("book/${book.id}")
                        },
                        onAddToCart = { viewModel.addToCart(book) }
                    )
                }
            }
        }
    }
}

@Composable
fun WishlistBookCard(
    book: Book,
    viewModel: BookViewModel,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Book Cover Image
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
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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

                    // Action buttons
                    Row {
                        // Add to Cart button
                        IconButton(
                            onClick = onAddToCart,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.AddShoppingCart,
                                contentDescription = "Add to Cart",
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Wishlist Heart Button
                        IconButton(
                            onClick = { viewModel.toggleWishlistForBook(book) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Favorite,
                                contentDescription = "Remove from Wishlist",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}