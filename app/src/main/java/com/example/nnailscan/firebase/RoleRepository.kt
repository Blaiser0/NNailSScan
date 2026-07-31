package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.AdminConfig
import com.example.nnailscan.data.model.AdminRequest
import com.example.nnailscan.data.model.UserProfile
import com.example.nnailscan.data.model.UserRole
import kotlinx.coroutines.flow.Flow

class RoleRepository(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
) {
    suspend fun loadCurrentRole(): UserRole {
        val user = authRepository.currentUser ?: return UserRole.USER
        val email = user.email.orEmpty()
        firestoreRepository.ensureDefaultAdmin(user.uid, email)
        val profile = firestoreRepository.getUserProfile(user.uid).getOrNull()
        return profile?.role ?: UserRole.USER
    }

    suspend fun submitAdminRequest(): Result<Unit> {
        val user = authRepository.currentUser ?: return Result.failure(Exception("No hay sesión activa."))
        val profile = authRepository.getUserProfile()
        return firestoreRepository.createAdminRequest(
            userId = user.uid,
            email = user.email.orEmpty(),
            fullName = profile?.fullName.orEmpty().ifBlank { "Usuario" },
        )
    }

    suspend fun hasPendingAdminRequest(): Boolean {
        val userId = authRepository.currentUser?.uid ?: return false
        return firestoreRepository.getPendingAdminRequestForUser(userId)
            .getOrNull() != null
    }

    fun observePendingAdminRequests(): Flow<List<AdminRequest>> =
        firestoreRepository.observePendingAdminRequests()

    fun observeAllUsers(): Flow<List<UserProfile>> =
        firestoreRepository.observeAllUsers()

    suspend fun approveAdminRequest(request: AdminRequest): Result<Unit> =
        firestoreRepository.resolveAdminRequest(
            requestId = request.id,
            userId = request.userId,
            approve = true,
        )

    suspend fun denyAdminRequest(request: AdminRequest): Result<Unit> =
        firestoreRepository.resolveAdminRequest(
            requestId = request.id,
            userId = request.userId,
            approve = false,
        )

    fun isDefaultAdminEmail(email: String): Boolean =
        email.equals(AdminConfig.DEFAULT_ADMIN_EMAIL, ignoreCase = true)

    suspend fun ensureAdminAccessReady() {
        val user = authRepository.currentUser ?: return
        firestoreRepository.ensureDefaultAdmin(user.uid, user.email.orEmpty())
    }
}
