package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.data.model.DictionaryTerm
import com.example.nnailscan.data.model.DictionaryTermDetail
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
        val data = mutableMapOf(
            "fullName" to profile.fullName,
            "email" to profile.email,
        )
        if (profile.photoUrl.isNotBlank()) {
            data["photoUrl"] = profile.photoUrl
        }
        firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .document(profile.uid)
            .set(data, SetOptions.merge())
            .await()
    }

    suspend fun updateUserPhotoUrl(uid: String, photoUrl: String): Result<Unit> = runCatching {
        firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .document(uid)
            .set(mapOf("photoUrl" to photoUrl), SetOptions.merge())
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
            photoUrl = snapshot.getString("photoUrl").orEmpty(),
        )
    }

    suspend fun saveScan(
        scanId: String,
        userId: String,
        result: String,
        rawLabel: String,
        confidence: Float,
        imageUrl: String,
        dictionaryTermId: String,
    ): Result<Unit> = runCatching {
        firestore.collection(FirebaseConfig.SCANS_COLLECTION)
            .document(scanId)
            .set(
                mapOf(
                    "userId" to userId,
                    "result" to result,
                    "rawLabel" to rawLabel,
                    "confidence" to confidence,
                    "imageUrl" to imageUrl,
                    "dictionaryTermId" to dictionaryTermId,
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
                        rawLabel = document.getString("rawLabel").orEmpty(),
                        confidence = document.getDouble("confidence")?.toFloat() ?: 0f,
                        imageUrl = document.getString("imageUrl").orEmpty(),
                        dictionaryTermId = document.getString("dictionaryTermId").orEmpty(),
                        createdAt = document.getTimestamp("createdAt"),
                    )
                }
                trySend(records)
            }

        awaitClose { registration.remove() }
    }

    suspend fun dictionaryTermHasImage(termId: String): Result<Boolean> = runCatching {
        val snapshot = firestore.collection(FirebaseConfig.DICTIONARY_TERMS_COLLECTION)
            .document(termId)
            .get()
            .await()
        snapshot.exists() && !snapshot.getString("imageUrl").isNullOrBlank()
    }

    suspend fun createDictionaryTerm(
        term: DictionaryTerm,
        imageUrl: String,
    ): Result<Unit> = runCatching {
        val detail = DictionaryContent.detailById(term.id)
        val data = mutableMapOf(
            "id" to term.id,
            "title" to term.title,
            "description" to term.description,
            "imageUrl" to imageUrl,
        )
        detail?.let {
            data["symptoms"] = it.symptoms
            data["causes"] = it.causes
            data["causesSectionTitle"] = it.causesSectionTitle
            data["scanDescription"] = it.scanDescription
            data["recommendations"] = it.recommendations
        }
        firestore.collection(FirebaseConfig.DICTIONARY_TERMS_COLLECTION)
            .document(term.id)
            .set(data)
            .await()
    }

    fun observeDictionaryTerms(): Flow<Map<String, DictionaryTermDetail>> = callbackFlow {
        val registration = firestore.collection(FirebaseConfig.DICTIONARY_TERMS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyMap())
                    return@addSnapshotListener
                }

                val terms = snapshot?.documents.orEmpty().associate { document ->
                    document.id to document.toDictionaryTermDetail()
                }
                trySend(terms)
            }

        awaitClose { registration.remove() }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toDictionaryTermDetail(): DictionaryTermDetail =
        DictionaryTermDetail(
            id = id,
            title = getString("title").orEmpty(),
            description = getString("description").orEmpty(),
            symptoms = getString("symptoms").orEmpty(),
            causes = getString("causes").orEmpty(),
            causesSectionTitle = getString("causesSectionTitle").orEmpty(),
            scanDescription = getString("scanDescription").orEmpty(),
            recommendations = getString("recommendations").orEmpty(),
            imageUrl = getString("imageUrl").orEmpty(),
        )
}
