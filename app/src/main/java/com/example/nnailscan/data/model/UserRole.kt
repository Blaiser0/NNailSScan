package com.example.nnailscan.data.model

enum class UserRole(val firestoreValue: String) {
    USER("user"),
    ADMIN("admin"),
    ;

    companion object {
        fun fromFirestore(value: String?): UserRole =
            entries.firstOrNull { it.firestoreValue == value } ?: USER
    }
}

object AdminConfig {
    const val DEFAULT_ADMIN_EMAIL = "snakercher@gmail.com"
}
