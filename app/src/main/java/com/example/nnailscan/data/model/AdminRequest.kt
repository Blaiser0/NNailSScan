package com.example.nnailscan.data.model

import com.google.firebase.Timestamp

enum class AdminRequestStatus(val firestoreValue: String) {
    PENDING("pending"),
    APPROVED("approved"),
    DENIED("denied"),
    ;

    companion object {
        fun fromFirestore(value: String?): AdminRequestStatus =
            entries.firstOrNull { it.firestoreValue == value } ?: PENDING
    }
}

data class AdminRequest(
    val id: String = "",
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val status: AdminRequestStatus = AdminRequestStatus.PENDING,
    val createdAt: Timestamp? = null,
)
