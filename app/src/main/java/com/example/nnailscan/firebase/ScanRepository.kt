package com.example.nnailscan.firebase

import android.content.Context
import android.graphics.Bitmap
import com.example.nnailscan.NailClassifier
import com.example.nnailscan.navigation.ScanSessionState
import com.example.nnailscan.util.BitmapCompressor
import com.example.nnailscan.util.formatClassificationLabel
import com.example.nnailscan.util.mapLabelToDictionaryTermId
import java.util.UUID

class ScanRepository(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
) {
    suspend fun processAndPersistScan(
        context: Context,
        userId: String,
        bitmap: Bitmap,
    ): Result<ScanSessionState.Payload> = runCatching {
        val scanId = UUID.randomUUID().toString()
        val scannedAtMillis = System.currentTimeMillis()

        NailClassifier(context.applicationContext).use { classifier ->
            val (rawLabel, confidence) = classifier.classifyImage(bitmap)
            val formattedLabel = formatClassificationLabel(rawLabel)
            val dictionaryTermId = mapLabelToDictionaryTermId(rawLabel)
            val jpegBytes = BitmapCompressor.toJpeg(bitmap)

            val imageUrl = storageRepository
                .uploadScanImage(userId, scanId, jpegBytes)
                .getOrThrow()

            val userFullName = authRepository.getUserProfile()?.fullName.orEmpty()

            firestoreRepository.saveScan(
                scanId = scanId,
                userId = userId,
                userFullName = userFullName,
                result = formattedLabel,
                rawLabel = rawLabel,
                confidence = confidence,
                imageUrl = imageUrl,
                dictionaryTermId = dictionaryTermId,
            ).getOrThrow()

            ScanSessionState.Payload(
                bitmap = bitmap,
                rawLabel = rawLabel,
                formattedLabel = formattedLabel,
                confidence = confidence,
                dictionaryTermId = dictionaryTermId,
                scannedAtMillis = scannedAtMillis,
                scanId = scanId,
                imageUrl = imageUrl,
            )
        }
    }
}
