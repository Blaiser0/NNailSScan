package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.util.PasswordValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean,
        onSuccess: () -> Unit,
    ) {
        val validationError = validate(
            fullName = fullName,
            email = email,
            password = password,
            confirmPassword = confirmPassword,
            termsAccepted = termsAccepted,
        )
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signUp(
                email = email,
                password = password,
                fullName = fullName,
            )
            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = { onSuccess() },
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

    private fun validate(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean,
    ): String? = when {
        fullName.isBlank() -> "Ingresa tu nombre completo."
        email.isBlank() -> "Ingresa tu correo electrónico."
        password.isBlank() -> "Ingresa una contraseña."
        confirmPassword.isBlank() -> "Confirma tu contraseña."
        password != confirmPassword -> "Las contraseñas no coinciden."
        PasswordValidator.validate(password) != null -> PasswordValidator.REQUIREMENTS_MESSAGE
        !termsAccepted -> "Debes aceptar los Términos y condiciones."
        else -> null
    }
}
