package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.data.model.UserProfile
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val firestore get() = FirebaseConfig.firestore

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .document(profile.uid)
            .set(
                mapOf(
                    "fullName" to profile.fullName,
                    "email" to profile.email,
                    "createdAt" to Timestamp.now(),
                ),
            )
            .await()
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .document(profile.uid)
            .set(
                mapOf(
                    "fullName" to profile.fullName,
                    "email" to profile.email,
                ),
                SetOptions.merge(),
            )
            .await()
    }

    suspend fun getUserProfile(uid: String): Result<UserProfile?> = runCatching {
        val snapshot = firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .document(uid)
            .get()
            .await()

        if (!snapshot.exists()) return@runCatching null

        UserProfile(
            uid = uid,
            fullName = snapshot.getString("fullName").orEmpty(),
            email = snapshot.getString("email").orEmpty(),
        )
    }

    suspend fun saveScan(userId: String, result: String): Result<Unit> = runCatching {
        firestore.collection(FirebaseConfig.SCANS_COLLECTION)
            .add(
                mapOf(
                    "userId" to userId,
                    "result" to result,
                    "createdAt" to Timestamp.now(),
                ),
            )
            .await()
    }

    fun observeRecentScans(userId: String, limit: Long = 3): Flow<List<ScanRecord>> =
        observeScans(userId, limit)

    fun observeAllScans(userId: String): Flow<List<ScanRecord>> =
        observeScans(userId, limit = 50)

    private fun observeScans(userId: String, limit: Long): Flow<List<ScanRecord>> = callbackFlow {
        val registration = firestore.collection(FirebaseConfig.SCANS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val records = snapshot?.documents.orEmpty().map { document ->
                    ScanRecord(
                        id = document.id,
                        userId = document.getString("userId").orEmpty(),
                        result = document.getString("result").orEmpty(),
                        createdAt = document.getTimestamp("createdAt"),
                    )
                }
                trySend(records)
            }

        awaitClose { registration.remove() }
    }
}
