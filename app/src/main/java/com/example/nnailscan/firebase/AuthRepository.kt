package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.UserProfile
import com.example.nnailscan.util.PasswordValidator
import com.google.firebase.auth.ActionCodeSettings
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

    suspend fun updateUserProfile(fullName: String): Result<Unit> = runCatching {
        val user = currentUser ?: error("No hay sesión activa.")
        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            error("El nombre no puede estar vacío.")
        }

        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(trimmedName)
                .build(),
        ).await()

        firestoreRepository.updateUserProfile(
            UserProfile(
                uid = user.uid,
                fullName = trimmedName,
                email = user.email.orEmpty(),
            ),
        ).getOrThrow()
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(Exception(mapAuthError(it))) },
    )

    fun signOut() {
        auth.signOut()
    }

    suspend fun sendAccountVerificationEmail(email: String): Result<Unit> = runCatching {
        val trimmedEmail = email.trim()
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            // Página informativa; el cambio de contraseña ocurre solo en la app móvil.
            .setUrl(VERIFICATION_COMPLETE_URL)
            .setHandleCodeInApp(true)
            .setAndroidPackageName(
                ANDROID_PACKAGE_NAME,
                true,
                MINIMUM_APP_VERSION,
            )
            .build()

        auth.sendPasswordResetEmail(trimmedEmail, actionCodeSettings).await()
    }.fold(
        onSuccess = { Result.success(Unit) },
        onFailure = { Result.failure(Exception(mapPasswordResetError(it))) },
    )

    /** Solo valida el enlace; no modifica la contraseña. */
    suspend fun verifyAccountOwnership(oobCode: String, expectedEmail: String): Result<String> =
        verifyEmailForPasswordReset(oobCode).fold(
            onSuccess = { verifiedEmail ->
                if (expectedEmail.isNotBlank() &&
                    !verifiedEmail.equals(expectedEmail.trim(), ignoreCase = true)
                ) {
                    Result.failure(
                        Exception("El enlace no corresponde al correo que ingresaste."),
                    )
                } else {
                    Result.success(verifiedEmail)
                }
            },
            onFailure = { Result.failure(it) },
        )

    /** Único punto donde se actualiza la contraseña en el flujo de recuperación. */
    suspend fun updatePasswordAfterVerification(
        oobCode: String,
        newPassword: String,
    ): Result<Unit> = confirmPasswordReset(oobCode, newPassword)

    suspend fun confirmPasswordReset(
        oobCode: String,
        newPassword: String,
    ): Result<Unit> {
        PasswordValidator.validate(newPassword)?.let { message ->
            return Result.failure(Exception(message))
        }
        return runCatching {
            auth.confirmPasswordReset(oobCode, newPassword).await()
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(Exception(mapPasswordResetError(it))) },
        )
    }

    suspend fun verifyEmailForPasswordReset(oobCode: String): Result<String> = runCatching {
        val verifiedEmail = auth.verifyPasswordResetCode(oobCode).await()
        verifiedEmail
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(Exception(mapPasswordResetError(it))) },
    )

    private fun mapPasswordResetError(error: Throwable): String {
        if (error is FirebaseAuthException) {
            return when (error.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Correo electrónico no válido."
                "ERROR_USER_NOT_FOUND" -> "No existe una cuenta registrada con este correo."
                "ERROR_INVALID_ACTION_CODE" -> "El enlace de recuperación no es válido."
                "ERROR_EXPIRED_ACTION_CODE" -> "El enlace de recuperación ha expirado. Solicita uno nuevo."
                "ERROR_WEAK_PASSWORD" -> PasswordValidator.REQUIREMENTS_MESSAGE
                "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Inténtalo más tarde."
                else -> mapAuthError(error)
            }
        }
        return error.message ?: "No se pudo completar la recuperación. Inténtalo de nuevo."
    }

    companion object {
        private const val ANDROID_PACKAGE_NAME = "com.example.nnailscan"
        private const val MINIMUM_APP_VERSION = "1"
        private const val VERIFICATION_COMPLETE_URL =
            "https://nailscan-65b49.firebaseapp.com/verification-complete"
    }

    fun mapAuthError(error: Throwable): String {
        if (error is FirebaseAuthException) {
            return when (error.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado."
                "ERROR_WEAK_PASSWORD" -> PasswordValidator.REQUIREMENTS_MESSAGE
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
