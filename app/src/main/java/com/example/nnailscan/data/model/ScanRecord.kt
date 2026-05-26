package com.example.nnailscan.data.model

import com.google.firebase.Timestamp

data class ScanRecord(
    val id: String = "",
    val userId: String = "",
    val result: String = "",
    val createdAt: Timestamp? = null,
)
