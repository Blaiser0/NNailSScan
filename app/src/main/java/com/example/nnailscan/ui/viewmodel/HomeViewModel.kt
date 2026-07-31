package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.data.model.UserRole
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.firebase.FirestoreRepository
import com.example.nnailscan.firebase.RoleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClassificationStat(
    val label: String,
    val count: Int,
)

data class HomeUiState(
    val userName: String = "Usuario",
    val photoUrl: String = "",
    val isAdmin: Boolean = false,
    val recentScans: List<ScanRecord> = emptyList(),
    val classificationStats: List<ClassificationStat> = emptyList(),
    val totalScans: Int = 0,
    val userNamesById: Map<String, String> = emptyMap(),
    val isLoading: Boolean = true,
)

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val roleRepository: RoleRepository = RoleRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var scansJob: Job? = null
    private var statsJob: Job? = null
    private var usersJob: Job? = null

    init {
        refreshProfile()
    }

    fun refreshProfile() {
        viewModelScope.launch {
            val profile = authRepository.getUserProfile()
            _uiState.update {
                it.copy(
                    userName = profile?.fullName?.ifBlank { null } ?: "Usuario",
                    photoUrl = profile?.photoUrl.orEmpty(),
                    isAdmin = profile?.role == UserRole.ADMIN,
                    isLoading = false,
                )
            }
        }
    }

    fun bindAdminViewMode(isAdminViewMode: Boolean) {
        scansJob?.cancel()
        statsJob?.cancel()
        usersJob?.cancel()

        val userId = authRepository.currentUser?.uid
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
                firestoreRepository.observeRecentAppScans().collect { scans ->
                    _uiState.update { it.copy(recentScans = scans) }
                }
            }
            statsJob = viewModelScope.launch {
                roleRepository.ensureAdminAccessReady()
                firestoreRepository.observeAllAppScansForStats().collect { scans ->
                    _uiState.update {
                        it.copy(
                            classificationStats = buildClassificationStats(scans),
                            totalScans = scans.size,
                        )
                    }
                }
            }
            return
        }

        _uiState.update { it.copy(userNamesById = emptyMap()) }
        if (userId == null) return
        scansJob = viewModelScope.launch {
            firestoreRepository.observeRecentScans(userId).collect { scans ->
                _uiState.update {
                    it.copy(
                        recentScans = scans,
                        classificationStats = emptyList(),
                        totalScans = scans.size,
                    )
                }
            }
        }
    }

    private fun buildClassificationStats(scans: List<ScanRecord>): List<ClassificationStat> =
        scans
            .groupBy { scan -> scan.result.ifBlank { scan.rawLabel }.ifBlank { "Sin clasificar" } }
            .map { (label, items) -> ClassificationStat(label = label, count = items.size) }
            .sortedByDescending { it.count }
}
