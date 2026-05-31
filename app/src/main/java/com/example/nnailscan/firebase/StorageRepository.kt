package com.example.nnailscan.firebase

import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await

class StorageRepository {
    private val storage get() = FirebaseConfig.storage

    suspend fun uploadProfileImage(
        userId: String,
        jpegBytes: ByteArray,
    ): Result<String> = uploadImage(
        path = "users/$userId/profile/profile_image.jpg",
        jpegBytes = jpegBytes,
        contentType = "image/jpeg",
    )

    suspend fun uploadScanImage(
        userId: String,
        scanId: String,
        jpegBytes: ByteArray,
    ): Result<String> = uploadImage(
        path = "users/$userId/scans/$scanId.jpg",
        jpegBytes = jpegBytes,
        contentType = "image/jpeg",
    )

    suspend fun uploadDictionaryImage(
        termId: String,
        pngBytes: ByteArray,
    ): Result<String> = uploadImage(
        path = "dictionary/$termId.png",
        jpegBytes = pngBytes,
        contentType = "image/png",
    )

    suspend fun getDictionaryImageUrl(termId: String): Result<String> = runCatching {
        storage.reference.child("dictionary/$termId.png").downloadUrl.await().toString()
    }

    private suspend fun uploadImage(
        path: String,
        jpegBytes: ByteArray,
        contentType: String,
    ): Result<String> = runCatching {
        val reference = storage.reference.child(path)
        val metadata = StorageMetadata.Builder()
            .setContentType(contentType)
            .build()

        reference.putBytes(jpegBytes, metadata).await()
        reference.downloadUrl.await().toString()
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { error ->
            Result.failure(
                Exception(mapStorageError(error), error),
            )
        },
    )

    private fun mapStorageError(error: Throwable): String {
        val message = error.message.orEmpty()
        return when {
            message.contains("object does not exist", ignoreCase = true) ->
                "Firebase Storage no está configurado. Activa Storage en Firebase Console y despliega storage.rules."

            message.contains("permission", ignoreCase = true) ||
                message.contains("unauthorized", ignoreCase = true) ||
                message.contains("403", ignoreCase = true) ->
                "No tienes permiso para guardar la imagen. Verifica las reglas de Storage en Firebase."

            message.contains("network", ignoreCase = true) ->
                "Error de red al subir la imagen. Revisa tu conexión."

            else -> "No se pudo guardar la imagen: ${error.message ?: "error desconocido"}"
        }
    }
}
