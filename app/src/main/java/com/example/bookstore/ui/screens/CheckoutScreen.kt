package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: BookViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Checkout") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (uiState.cartItems.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Your cart is empty")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("home") }) {
                    Text("Browse Books")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Order summary
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            "Order Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        uiState.cartItems.forEach { cartItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${cartItem.book.title} x${cartItem.quantity}")
                                Text("${"%.2f".format(cartItem.book.price * cartItem.quantity)} DT")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider()

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${"%.2f".format(viewModel.calculateCartTotal())} DT",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Shipping info
                if (authState.isAuthenticated) {
                    authState.currentUser?.let { user ->
                        Card {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    "Shipping Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Name: ${user.name}")
                                Text("Address: ${user.address}")
                                Text("Phone: ${user.phone}")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Place order button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (authState.isAuthenticated && authState.currentUser != null) {
                                // Create order
                                val order = com.example.bookstore.model.Order(
                                    userId = authState.currentUser!!.id,
                                    books = uiState.cartItems.map { cartItem ->
                                        com.example.bookstore.model.OrderItem(
                                            bookId = cartItem.book.id,
                                            title = cartItem.book.title,
                                            quantity = cartItem.quantity,
                                            unitPrice = cartItem.book.price
                                        )
                                    },
                                    totalAmount = viewModel.calculateCartTotal(),
                                    shippingAddress = authState.currentUser!!.address
                                )

                                // Save order
                                viewModel.repository.placeOrder(order)

                                // Clear cart
                                viewModel.clearCart()

                                // Navigate to orders
                                navController.navigate("orders") {
                                    popUpTo("home") { inclusive = false }
                                }
                            } else {
                                navController.navigate("auth")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.cartItems.isNotEmpty()
                ) {
                    Text("Place Order")
                }
            }
        }
    }
}