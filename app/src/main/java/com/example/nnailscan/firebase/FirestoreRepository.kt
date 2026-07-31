package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.AdminRequest
import com.example.nnailscan.data.model.AdminRequestStatus
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.data.model.DictionaryTerm
import com.example.nnailscan.data.model.DictionaryTermDetail
import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.data.model.UserProfile
import com.example.nnailscan.data.model.UserRole
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
                    "role" to profile.role.firestoreValue,
                    "createdAt" to Timestamp.now(),
                ),
            )
            .await()
    }

    suspend fun updateUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        val data = mutableMapOf(
            "fullName" to profile.fullName,
            "email" to profile.email,
            "role" to profile.role.firestoreValue,
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
            role = UserRole.fromFirestore(snapshot.getString("role")),
        )
    }

    suspend fun updateUserRole(uid: String, role: UserRole): Result<Unit> = runCatching {
        firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .document(uid)
            .set(mapOf("role" to role.firestoreValue), SetOptions.merge())
            .await()
    }

    suspend fun ensureDefaultAdmin(uid: String, email: String): Result<Unit> = runCatching {
        if (email.equals(com.example.nnailscan.data.model.AdminConfig.DEFAULT_ADMIN_EMAIL, ignoreCase = true)) {
            firestore.collection(FirebaseConfig.USERS_COLLECTION)
                .document(uid)
                .set(mapOf("role" to UserRole.ADMIN.firestoreValue), SetOptions.merge())
                .await()
        }
    }

    fun observeAllUsers(): Flow<List<UserProfile>> = callbackFlow {
        val registration = firestore.collection(FirebaseConfig.USERS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = snapshot?.documents.orEmpty().map { document ->
                    UserProfile(
                        uid = document.id,
                        fullName = document.getString("fullName").orEmpty(),
                        email = document.getString("email").orEmpty(),
                        photoUrl = document.getString("photoUrl").orEmpty(),
                        role = UserRole.fromFirestore(document.getString("role")),
                    )
                }.sortedBy { it.fullName.lowercase() }
                trySend(users)
            }
        awaitClose { registration.remove() }
    }

    fun observeAllAppScans(limit: Long = 50): Flow<List<ScanRecord>> = callbackFlow {
        val registration = firestore.collection(FirebaseConfig.SCANS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().map { it.toScanRecord() })
            }
        awaitClose { registration.remove() }
    }

    fun observeRecentAppScans(limit: Long = 3): Flow<List<ScanRecord>> = observeAllAppScans(limit)

    fun observeAllAppScansForStats(): Flow<List<ScanRecord>> = callbackFlow {
        val registration = firestore.collection(FirebaseConfig.SCANS_COLLECTION)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(500)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().map { it.toScanRecord() })
            }
        awaitClose { registration.remove() }
    }

    suspend fun createAdminRequest(
        userId: String,
        email: String,
        fullName: String,
    ): Result<Unit> = runCatching {
        val existing = firestore.collection(FirebaseConfig.ADMIN_REQUESTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", AdminRequestStatus.PENDING.firestoreValue)
            .get()
            .await()
        if (!existing.isEmpty) return@runCatching

        firestore.collection(FirebaseConfig.ADMIN_REQUESTS_COLLECTION)
            .document()
            .set(
                mapOf(
                    "userId" to userId,
                    "email" to email,
                    "fullName" to fullName,
                    "status" to AdminRequestStatus.PENDING.firestoreValue,
                    "createdAt" to Timestamp.now(),
                ),
            )
            .await()
    }

    suspend fun getPendingAdminRequestForUser(userId: String): Result<AdminRequest?> = runCatching {
        val snapshot = firestore.collection(FirebaseConfig.ADMIN_REQUESTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("status", AdminRequestStatus.PENDING.firestoreValue)
            .limit(1)
            .get()
            .await()
        snapshot.documents.firstOrNull()?.toAdminRequest()
    }

    fun observePendingAdminRequests(): Flow<List<AdminRequest>> = callbackFlow {
        val registration = firestore.collection(FirebaseConfig.ADMIN_REQUESTS_COLLECTION)
            .whereEqualTo("status", AdminRequestStatus.PENDING.firestoreValue)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents.orEmpty().map { it.toAdminRequest() })
            }
        awaitClose { registration.remove() }
    }

    suspend fun resolveAdminRequest(
        requestId: String,
        userId: String,
        approve: Boolean,
    ): Result<Unit> = runCatching {
        val status = if (approve) AdminRequestStatus.APPROVED else AdminRequestStatus.DENIED
        firestore.collection(FirebaseConfig.ADMIN_REQUESTS_COLLECTION)
            .document(requestId)
            .update("status", status.firestoreValue)
            .await()
        if (approve) {
            updateUserRole(userId, UserRole.ADMIN).getOrThrow()
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toScanRecord(): ScanRecord =
        ScanRecord(
            id = id,
            userId = getString("userId").orEmpty(),
            userFullName = getString("userFullName").orEmpty(),
            result = getString("result").orEmpty(),
            rawLabel = getString("rawLabel").orEmpty(),
            confidence = getDouble("confidence")?.toFloat() ?: 0f,
            imageUrl = getString("imageUrl").orEmpty(),
            dictionaryTermId = getString("dictionaryTermId").orEmpty(),
            createdAt = getTimestamp("createdAt"),
        )

    private fun com.google.firebase.firestore.DocumentSnapshot.toAdminRequest(): AdminRequest =
        AdminRequest(
            id = id,
            userId = getString("userId").orEmpty(),
            email = getString("email").orEmpty(),
            fullName = getString("fullName").orEmpty(),
            status = AdminRequestStatus.fromFirestore(getString("status")),
            createdAt = getTimestamp("createdAt"),
        )

    suspend fun saveScan(
        scanId: String,
        userId: String,
        userFullName: String,
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
                    "userFullName" to userFullName,
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
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val records = snapshot?.documents.orEmpty().map { document ->
                    document.toScanRecord()
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
