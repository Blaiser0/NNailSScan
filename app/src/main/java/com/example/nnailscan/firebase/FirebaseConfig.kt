package com.example.nnailscan.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

/**
 * Punto central de acceso a servicios Firebase.
 */
object FirebaseConfig {
    const val USERS_COLLECTION = "users"
    const val SCANS_COLLECTION = "scans"
    const val DICTIONARY_TERMS_COLLECTION = "dictionary_terms"
    private const val FALLBACK_STORAGE_BUCKET = "gs://nailscan-65b49.firebasestorage.app"

    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    val storage: FirebaseStorage
        get() {
            val bucket = FirebaseApp.getInstance().options.storageBucket
            val bucketUrl = when {
                bucket.isNullOrBlank() -> FALLBACK_STORAGE_BUCKET
                bucket.startsWith("gs://") -> bucket
                else -> "gs://$bucket"
            }
            return FirebaseStorage.getInstance(bucketUrl)
        }
}
