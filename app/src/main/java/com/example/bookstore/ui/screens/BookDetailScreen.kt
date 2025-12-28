package com.example.bookstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.bookstore.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book?,
    viewModel: BookViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    if (book == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Book Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Book Cover
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = book.coverImageUrl,
                    error = androidx.compose.ui.res.painterResource(android.R.drawable.ic_menu_report_image)
                ),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )
        }

        // Book Details Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Wishlist Button
                    IconButton(
                        onClick = { viewModel.toggleWishlistForBook(book) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            if (uiState.books.find { it.id == book.id }?.isInWishlist == true)
                                Icons.Default.Favorite
                            else
                                Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (uiState.books.find { it.id == book.id }?.isInWishlist == true)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Author
                Text(
                    text = "by ${book.author}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "%.1f".format(book.rating),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "(${book.reviews} reviews)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Price
                    Text(
                        text = book.formattedPrice(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Book Details Grid
                Text(
                    text = "Book Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BookDetailRow(
                        icon = Icons.Default.Category,
                        title = "Category",
                        value = book.category
                    )

                    BookDetailRow(
                        icon = Icons.Default.Business,
                        title = "Publisher",
                        value = book.publisher
                    )

                    BookDetailRow(
                        icon = Icons.Default.CalendarToday,
                        title = "Published Date",
                        value = book.publishedDate
                    )

                    BookDetailRow(
                        icon = Icons.Default.MenuBook,
                        title = "Pages",
                        value = "${book.pages} pages"
                    )

                    BookDetailRow(
                        icon = Icons.Default.Numbers,
                        title = "ISBN",
                        value = book.isbn
                    )

                    BookDetailRow(
                        icon = Icons.Default.Language,
                        title = "Language",
                        value = book.language
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Add to Cart Button
                Button(
                    onClick = { viewModel.addToCart(book) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BookDetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}