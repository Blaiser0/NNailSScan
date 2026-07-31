package com.example.nnailscan.navigation

import android.graphics.Bitmap
import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.util.formatClassificationLabel
import com.example.nnailscan.util.mapLabelToDictionaryTermId

object ScanSessionState {
    data class Payload(
        val bitmap: Bitmap? = null,
        val rawLabel: String,
        val formattedLabel: String,
        val confidence: Float,
        val dictionaryTermId: String,
        val scannedAtMillis: Long,
        val scanId: String = "",
        val imageUrl: String = "",
    )

    var current: Payload? = null

    fun openFromRecord(record: ScanRecord) {
        current = Payload(
            bitmap = null,
            rawLabel = record.rawLabel,
            formattedLabel = record.result.ifBlank {
                formatClassificationLabel(record.rawLabel)
            },
            confidence = record.confidence,
            dictionaryTermId = record.dictionaryTermId.ifBlank {
                mapLabelToDictionaryTermId(record.rawLabel)
            },
            scannedAtMillis = record.createdAt?.toDate()?.time ?: System.currentTimeMillis(),
            scanId = record.id,
            imageUrl = record.imageUrl,
        )
    }

    fun clear() {
        current = null
    }
}
