package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    viewModel: BookViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val authState by authViewModel.uiState.collectAsState()
    val bookUiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }

    // Local state for form fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var ordersCount by remember { mutableStateOf(0) }

    // Track if we need to save changes
    var showSaveSuccess by remember { mutableStateOf(false) }

    // Initialize with current user data from auth state
    LaunchedEffect(authState.currentUser) {
        if (authState.isAuthenticated && authState.currentUser != null) {
            name = authState.currentUser!!.name
            email = authState.currentUser!!.email
            address = authState.currentUser!!.address
            phone = authState.currentUser!!.phone

            // Load orders count
            coroutineScope.launch {
                viewModel.repository.getOrdersByUser(authState.currentUser!!.id).collect { orders ->
                    ordersCount = orders.size
                }
            }
        }
    }

    // Show success message and hide after 2 seconds
    if (showSaveSuccess) {
        LaunchedEffect(showSaveSuccess) {
            kotlinx.coroutines.delay(2000)
            showSaveSuccess = false
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    "My Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (authState.isAuthenticated) {
                    // Edit and Logout buttons
                    Row {
                        // Edit button
                        if (isEditing) {
                            IconButton(
                                onClick = {
                                    isEditing = false
                                    // Reset to original values when canceling
                                    authState.currentUser?.let { user ->
                                        name = user.name
                                        address = user.address
                                        phone = user.phone
                                    }
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { isEditing = true },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Logout button
                        IconButton(
                            onClick = {
                                authViewModel.logout()
                                viewModel.clearCart()
                                navController.navigate("auth") {
                                    popUpTo("home") { inclusive = false }
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        )

        // Success message snackbar
        if (showSaveSuccess) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Profile updated successfully!",
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

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
                    Icons.Default.Person,
                    contentDescription = "Login",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Please login to view your profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Login to access your profile information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("auth") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate("auth") }) {
                    Text("Create Account")
                }
            }
        } else {
            // Profile content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // User info header
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "Personal Information",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Manage your account details",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile form
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            singleLine = true,
                            enabled = isEditing,
                            shape = MaterialTheme.shapes.medium
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            enabled = false,
                            shape = MaterialTheme.shapes.medium
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Address") },
                            singleLine = true,
                            enabled = isEditing,
                            shape = MaterialTheme.shapes.medium
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            enabled = isEditing,
                            shape = MaterialTheme.shapes.medium
                        )

                        // Save button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    authState.currentUser?.let { user ->
                                        // Create updated user
                                        val updatedUser = user.copy(
                                            name = name,
                                            address = address,
                                            phone = phone
                                        )
                                        // Save to database
                                        try {
                                            viewModel.repository.updateUser(updatedUser)

                                            // Update the auth view model state
                                            authViewModel.updateCurrentUser(updatedUser)

                                            // Exit edit mode
                                            isEditing = false

                                            // Show success message
                                            showSaveSuccess = true
                                        } catch (e: Exception) {
                                            // Handle error
                                            println("Failed to update profile: ${e.message}")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            enabled = name.isNotBlank() && address.isNotBlank() && phone.isNotBlank()
                        ) {
                            Text("Save Changes", fontWeight = FontWeight.Medium)
                        }

                        // Cancel button
                        OutlinedButton(
                            onClick = {
                                isEditing = false
                                // Reset to original values
                                authState.currentUser?.let { user ->
                                    name = user.name
                                    address = user.address
                                    phone = user.phone
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text("Cancel")
                        }
                    } else {
                        // Display mode
                        ProfileInfoRow(
                            icon = Icons.Default.Person,
                            label = "Name",
                            value = name
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = email
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.Home,
                            label = "Address",
                            value = address
                        )

                        ProfileInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Phone",
                            value = phone
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "Your Stats",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProfileStat(
                                    value = bookUiState.wishlistBooks.size.toString(),
                                    label = "Wishlist"
                                )

                                ProfileStat(
                                    value = ordersCount.toString(),
                                    label = "Orders"
                                )

                                ProfileStat(
                                    value = bookUiState.cartItems.size.toString(),
                                    label = "Cart"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("orders") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Orders", fontWeight = FontWeight.Medium)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("wishlist") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("My Wishlist", fontWeight = FontWeight.Medium)
                        }

                        OutlinedButton(
                            onClick = { navController.navigate("cart") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Shopping Cart", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                label,
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

@Composable
fun ProfileStat(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}