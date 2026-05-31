package com.example.nnailscan.data.model

import com.google.firebase.Timestamp

data class ScanRecord(
    val id: String = "",
    val userId: String = "",
    val result: String = "",
    val rawLabel: String = "",
    val confidence: Float = 0f,
    val imageUrl: String = "",
    val dictionaryTermId: String = "",
    val createdAt: Timestamp? = null,
)
