package com.example.nnailscan.firebase

import android.content.Context
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.data.model.DictionaryTerm

class DictionarySeedRepository(
    private val context: Context,
    private val authRepository: AuthRepository = AuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
) {
    suspend fun seedIfNeeded(): Result<Unit> = runCatching {
        if (authRepository.currentUser == null) return@runCatching

        DictionaryContent.terms.forEach { term ->
            if (firestoreRepository.dictionaryTermHasImage(term.id).getOrDefault(false)) {
                return@forEach
            }

            runCatching {
                val bytes = context.assets.open("categories/${term.id}.png").use { input ->
                    input.readBytes()
                }
                val imageUrl = storageRepository
                    .uploadDictionaryImage(term.id, bytes)
                    .getOrElse {
                        storageRepository.getDictionaryImageUrl(term.id).getOrThrow()
                    }

                firestoreRepository.createDictionaryTerm(
                    term = term,
                    imageUrl = imageUrl,
                ).getOrThrow()
            }
        }
    }
}
