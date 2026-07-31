package com.example.nnailscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nnailscan.data.model.AdminRequest
import com.example.nnailscan.data.model.UserProfile
import com.example.nnailscan.data.model.UserRole
import com.example.nnailscan.firebase.RoleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RoleUiState(
    val role: UserRole = UserRole.USER,
    val isAdminViewMode: Boolean = false,
    val hasPendingAdminRequest: Boolean = false,
    val isLoading: Boolean = true,
    val message: String? = null,
)

class RoleViewModel(
    private val roleRepository: RoleRepository = RoleRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoleUiState())
    val uiState = _uiState.asStateFlow()

    val isAdminCapable: Boolean
        get() = _uiState.value.role == UserRole.ADMIN

    val showAdminUi: Boolean
        get() = isAdminCapable && _uiState.value.isAdminViewMode

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = null) }
            val role = roleRepository.loadCurrentRole()
            val pending = roleRepository.hasPendingAdminRequest()
            _uiState.update {
                it.copy(
                    role = role,
                    hasPendingAdminRequest = pending,
                    isAdminViewMode = if (role == UserRole.ADMIN) it.isAdminViewMode else false,
                    isLoading = false,
                )
            }
        }
    }

    fun toggleAdminViewMode() {
        if (!isAdminCapable) return
        viewModelScope.launch {
            val enabling = !_uiState.value.isAdminViewMode
            if (enabling) {
                roleRepository.ensureAdminAccessReady()
            }
            _uiState.update { it.copy(isAdminViewMode = !it.isAdminViewMode) }
        }
    }

    fun requestAdminAccess(onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (_uiState.value.role == UserRole.ADMIN) {
                toggleAdminViewMode()
                onResult("Modo administrador activado.")
                return@launch
            }
            if (_uiState.value.hasPendingAdminRequest) {
                onResult("Ya tienes una petición de admin pendiente.")
                return@launch
            }
            roleRepository.submitAdminRequest().fold(
                onSuccess = {
                    _uiState.update { it.copy(hasPendingAdminRequest = true) }
                    onResult("Petición enviada. Un administrador la revisará.")
                },
                onFailure = { error ->
                    onResult(error.message ?: "No se pudo enviar la petición.")
                },
            )
        }
    }

    fun switchToUserMode() {
        _uiState.update { it.copy(isAdminViewMode = false) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class AdminRequestsUiState(
    val requests: List<AdminRequest> = emptyList(),
    val isProcessing: Boolean = false,
    val message: String? = null,
)

class AdminRequestsViewModel(
    private val roleRepository: RoleRepository = RoleRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminRequestsUiState())
    val uiState = _uiState.asStateFlow()

    private var requestsJob: Job? = null

    fun bindAdminViewMode(isAdminViewMode: Boolean) {
        requestsJob?.cancel()
        if (!isAdminViewMode) {
            _uiState.update { it.copy(requests = emptyList()) }
            return
        }
        requestsJob = viewModelScope.launch {
            roleRepository.ensureAdminAccessReady()
            roleRepository.observePendingAdminRequests().collect { requests ->
                _uiState.update { it.copy(requests = requests) }
            }
        }
    }

    fun approve(request: AdminRequest) {
        process(request) { roleRepository.approveAdminRequest(request) }
    }

    fun deny(request: AdminRequest) {
        process(request) { roleRepository.denyAdminRequest(request) }
    }

    private fun process(request: AdminRequest, action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, message = null) }
            action().fold(
                onSuccess = {
                    _uiState.update { it.copy(isProcessing = false, message = "Petición actualizada.") }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isProcessing = false, message = error.message ?: "Error al procesar.")
                    }
                },
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

data class AdminUsersUiState(
    val users: List<UserProfile> = emptyList(),
)

class AdminUsersViewModel(
    private val roleRepository: RoleRepository = RoleRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUsersUiState())
    val uiState = _uiState.asStateFlow()

    private var usersJob: Job? = null

    fun bindAdminViewMode(isAdminViewMode: Boolean) {
        usersJob?.cancel()
        if (!isAdminViewMode) {
            _uiState.update { it.copy(users = emptyList()) }
            return
        }
        usersJob = viewModelScope.launch {
            roleRepository.ensureAdminAccessReady()
            roleRepository.observeAllUsers().collect { users ->
                _uiState.update { it.copy(users = users) }
            }
        }
    }
}
