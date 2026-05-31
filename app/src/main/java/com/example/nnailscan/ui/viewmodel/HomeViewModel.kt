package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.firebase.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val userName: String = "Usuario",
    val photoUrl: String = "",
    val recentScans: List<ScanRecord> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refreshProfile()
        observeScans()
    }

    fun refreshProfile() {
        viewModelScope.launch {
            val profile = authRepository.getUserProfile()
            _uiState.update {
                it.copy(
                    userName = profile?.fullName?.ifBlank { null } ?: "Usuario",
                    photoUrl = profile?.photoUrl.orEmpty(),
                    isLoading = false,
                )
            }
        }
    }

    private fun observeScans() {
        val userId = authRepository.currentUser?.uid ?: return
        viewModelScope.launch {
            firestoreRepository.observeRecentScans(userId).collect { scans ->
                _uiState.update { it.copy(recentScans = scans) }
            }
        }
    }
}
