package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.firebase.FirestoreRepository
import com.example.nnailscan.firebase.RoleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val scans: List<ScanRecord> = emptyList(),
    val isAdminView: Boolean = false,
    val userNamesById: Map<String, String> = emptyMap(),
)

class HistoryViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val roleRepository: RoleRepository = RoleRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    private var scansJob: Job? = null
    private var usersJob: Job? = null

    fun bindAdminViewMode(isAdminViewMode: Boolean) {
        scansJob?.cancel()
        usersJob?.cancel()
        _uiState.update { it.copy(isAdminView = isAdminViewMode) }

        if (isAdminViewMode) {
            usersJob = viewModelScope.launch {
                firestoreRepository.observeAllUsers().collect { users ->
                    _uiState.update {
                        it.copy(
                            userNamesById = users.associate { user ->
                                user.uid to user.fullName.ifBlank { user.email }.ifBlank { "Usuario" }
                            },
                        )
                    }
                }
            }
            scansJob = viewModelScope.launch {
                roleRepository.ensureAdminAccessReady()
                firestoreRepository.observeAllAppScans().collect { scans ->
                    _uiState.update { it.copy(scans = scans) }
                }
            }
            return
        }

        _uiState.update { it.copy(userNamesById = emptyMap()) }
        val userId = authRepository.currentUser?.uid ?: return
        scansJob = viewModelScope.launch {
            firestoreRepository.observeAllScans(userId).collect { scans ->
                _uiState.update { it.copy(scans = scans) }
            }
        }
    }
}
