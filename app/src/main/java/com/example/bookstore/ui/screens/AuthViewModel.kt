package com.example.bookstore.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookstore.data.BookRepository
import com.example.bookstore.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val registrationSuccess: Boolean = false
)

class AuthViewModel(private val repository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter email and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val user = repository.login(email, password)
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = true,
                            currentUser = user,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Invalid email or password"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Login failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun register(name: String, email: String, password: String, address: String, phone: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all required fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Check if user already exists
                val existingUser = repository.getUserById(email.hashCode())
                if (existingUser != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User with this email already exists"
                        )
                    }
                    return@launch
                }

                val user = User(
                    email = email,
                    password = password,
                    name = name,
                    address = address,
                    phone = phone
                )

                val userId = repository.registerUser(user)
                if (userId > 0) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccess = true,
                            currentUser = user.copy(id = userId.toInt())
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Registration failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun logout() {
        _uiState.update {
            AuthUiState()
        }
    }

    fun updateCurrentUser(updatedUser: User) {
        _uiState.update { it.copy(currentUser = updatedUser) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearRegistrationSuccess() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }
}