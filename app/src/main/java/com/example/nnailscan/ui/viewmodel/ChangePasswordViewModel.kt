package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.navigation.PasswordResetState
import com.example.nnailscan.util.PasswordValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class ChangePasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun changePassword(
        newPassword: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
    ) {
        val oobCode = PasswordResetState.oobCode
        if (oobCode.isNullOrBlank() || !PasswordResetState.isEmailVerified) {
            _uiState.update {
                it.copy(errorMessage = "Primero verifica tu correo desde el enlace enviado.")
            }
            return
        }

        val validationError = when {
            newPassword.isBlank() -> "Ingresa una nueva contraseña."
            confirmPassword.isBlank() -> "Confirma tu nueva contraseña."
            newPassword != confirmPassword -> "Las contraseñas no coinciden."
            else -> PasswordValidator.validate(newPassword)
        }
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.updatePasswordAfterVerification(oobCode, newPassword)
            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = {
                    PasswordResetState.clear()
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
