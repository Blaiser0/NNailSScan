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
    )

    var current: Payload? = null

    fun clear() {
        current = null
    }
}
