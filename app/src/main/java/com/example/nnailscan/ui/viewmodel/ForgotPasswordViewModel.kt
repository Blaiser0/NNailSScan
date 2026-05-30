package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.navigation.PasswordResetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun sendResetLink(
        email: String,
        onSuccess: () -> Unit,
    ) {
        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu correo electrónico.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.sendAccountVerificationEmail(email)
            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = {
                    PasswordResetState.email = email.trim()
                    PasswordResetState.oobCode = null
                    PasswordResetState.isEmailVerified = false
                    PasswordResetState.verifiedEmail = null
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { state ->
                        state.copy(errorMessage = error.message)
                    }
                },
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
