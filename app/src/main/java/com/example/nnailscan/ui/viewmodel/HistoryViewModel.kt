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

data class HistoryUiState(
    val scans: List<ScanRecord> = emptyList(),
)

class HistoryViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val userId = authRepository.currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                firestoreRepository.observeAllScans(userId).collect { scans ->
                    _uiState.update { it.copy(scans = scans) }
                }
            }
        }
    }
}
