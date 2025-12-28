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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: BookViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var orders by remember { mutableStateOf(emptyList<com.example.bookstore.model.Order>()) }

    LaunchedEffect(authState.currentUser) {
        if (authState.isAuthenticated && authState.currentUser != null) {
            coroutineScope.launch {
                viewModel.repository.getOrdersByUser(authState.currentUser!!.id)
                    .collect { orderList ->
                        orders = orderList
                    }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My Orders") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (!authState.isAuthenticated) {
            // Not logged in
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Login,
                    contentDescription = "Login",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Please login to view your orders")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("auth") }) {
                    Text("Login")
                }
            }
        } else if (orders.isEmpty()) {
            // No orders
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = "No Orders",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No orders yet")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Start shopping to see your orders here")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate("home") }) {
                    Text("Browse Books")
                }
            }
        } else {
            // Display orders
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    OrderCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: com.example.bookstore.model.Order) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status.name,
                    color = when (order.status) {
                        com.example.bookstore.model.OrderStatus.DELIVERED -> MaterialTheme.colorScheme.primary
                        com.example.bookstore.model.OrderStatus.SHIPPED -> MaterialTheme.colorScheme.secondary
                        com.example.bookstore.model.OrderStatus.PENDING -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.error
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Placed: ${java.text.SimpleDateFormat("MMM dd, yyyy").format(java.util.Date(order.orderDate))}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            order.books.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.title} x${item.quantity}")
                    Text("${"%.2f".format(item.subtotal())} DT")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text(
                    order.formattedTotal(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}