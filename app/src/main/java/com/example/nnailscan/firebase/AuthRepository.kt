package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
) {
    private val auth get() = FirebaseConfig.auth

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(Exception(mapAuthError(it))) },
    )

    suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
    ): Result<Unit> = runCatching {
        val trimmedEmail = email.trim()
        val trimmedName = fullName.trim()

        val result = auth.createUserWithEmailAndPassword(trimmedEmail, password).await()
        val user = result.user ?: error("No se pudo crear la cuenta.")

        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(trimmedName)
                .build(),
        ).await()

        firestoreRepository.saveUserProfile(
            UserProfile(
                uid = user.uid,
                fullName = trimmedName,
                email = trimmedEmail,
            ),
        ).getOrThrow()
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(Exception(mapAuthError(it))) },
    )

    suspend fun getUserProfile(): UserProfile? {
        val uid = currentUser?.uid ?: return null
        return firestoreRepository.getUserProfile(uid).getOrNull()
            ?: currentUser?.let { user ->
                UserProfile(
                    uid = user.uid,
                    fullName = user.displayName.orEmpty().ifBlank { "Usuario" },
                    email = user.email.orEmpty(),
                )
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun mapAuthError(error: Throwable): String {
        if (error is FirebaseAuthException) {
            return when (error.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado."
                "ERROR_WEAK_PASSWORD" -> "La contraseña es demasiado débil (mínimo 6 caracteres)."
                "ERROR_INVALID_EMAIL" -> "Correo electrónico no válido."
                "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este correo."
                "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" -> "Correo o contraseña incorrectos."
                "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada."
                "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Inténtalo más tarde."
                else -> error.localizedMessage ?: "Error de autenticación. Inténtalo de nuevo."
            }
        }
        return error.message ?: "Error de autenticación. Inténtalo de nuevo."
    }
}
