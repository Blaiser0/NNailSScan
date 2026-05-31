package com.example.nnailscan.navigation

import android.graphics.Bitmap

object ScanSessionState {
    data class Payload(
        val bitmap: Bitmap,
        val rawLabel: String,
        val formattedLabel: String,
        val confidence: Float,
        val dictionaryTermId: String,
        val scannedAtMillis: Long,
        val scanId: String = "",
        val imageUrl: String = "",
    )

    var current: Payload? = null

    fun clear() {
        current = null
    }
}
