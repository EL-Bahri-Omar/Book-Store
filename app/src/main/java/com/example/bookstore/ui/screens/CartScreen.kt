package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: BookViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("Shopping Cart") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (uiState.cartItems.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearCart() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Cart")
                    }
                }
            }
        )

        if (uiState.cartItems.isEmpty()) {
            // Empty cart
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Empty Cart",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your cart is empty",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add some books to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(onClick = { navController.navigate("home") }) {
                    Text("Browse Books")
                }
            }
        } else {
            // Cart items
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.cartItems) { cartItem ->
                    CartItemCard(
                        cartItem = cartItem,
                        onIncrease = {
                            viewModel.updateCartQuantity(cartItem.book.id, cartItem.quantity + 1)
                        },
                        onDecrease = {
                            viewModel.updateCartQuantity(cartItem.book.id, cartItem.quantity - 1)
                        },
                        onRemove = {
                            viewModel.removeFromCart(cartItem.book.id)
                        }
                    )
                }
            }

            // Checkout section
            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal")
                        Text(
                            "${"%.2f".format(viewModel.calculateCartTotal())} DT",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping")
                        Text("Free", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "${"%.2f".format(viewModel.calculateCartTotal())} DT",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (authState.isAuthenticated) {
                                navController.navigate("checkout")
                            } else {
                                navController.navigate("auth")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.cartItems.isNotEmpty()
                    ) {
                        Text("Proceed to Checkout")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Book info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.book.title,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = cartItem.book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${"%.2f".format(cartItem.book.price)} DT",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Quantity controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDecrease,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }

                        Text(
                            text = cartItem.quantity.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = onIncrease,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }
            }

            // Remove button
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}