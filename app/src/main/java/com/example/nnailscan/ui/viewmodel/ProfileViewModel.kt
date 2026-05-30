package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccess = false) }
            val profile = authRepository.getUserProfile()
            _uiState.update {
                it.copy(
                    fullName = profile?.fullName.orEmpty(),
                    email = profile?.email.orEmpty(),
                    isLoading = false,
                )
            }
        }
    }

    fun updateProfile(fullName: String, onSuccess: () -> Unit) {
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu nombre completo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccess = false) }
            val result = authRepository.updateUserProfile(trimmedName)
            _uiState.update { it.copy(isSaving = false) }
            result.fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(fullName = trimmedName, saveSuccess = true)
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                },
            )
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        authRepository.signOut()
        onSignedOut()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}
