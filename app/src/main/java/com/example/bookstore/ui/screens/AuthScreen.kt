package com.example.bookstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate("home") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    LaunchedEffect(authState.registrationSuccess) {
        if (authState.registrationSuccess) {
            isLoginMode = true
            authViewModel.clearRegistrationSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Welcome Back" else "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isLoginMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        if (!isLoginMode) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Address") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Error message
        authState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Login/Register button
        Button(
            onClick = {
                if (isLoginMode) {
                    authViewModel.login(email, password)
                } else {
                    authViewModel.register(name, email, password, address, phone)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !authState.isLoading
        ) {
            if (authState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (isLoginMode) "Login" else "Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch mode
        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
                authViewModel.clearError()
            }
        ) {
            Text(
                text = if (isLoginMode)
                    "Don't have an account? Register"
                else
                    "Already have an account? Login"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue as Guest
        if (isLoginMode) {
            TextButton(onClick = { navController.navigate("home") }) {
                Text("Continue as Guest")
            }
        }
    }
}