package com.example.nnailscan.firebase

import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.data.model.DictionaryTerm
import com.example.nnailscan.data.model.DictionaryTermDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DictionaryRepository(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository(),
) {
    fun observeTerms(): Flow<List<DictionaryTerm>> =
        firestoreRepository.observeDictionaryTerms().map { remoteTerms ->
            DictionaryContent.terms.map { local ->
                mergeTerm(local, remoteTerms[local.id])
            }
        }

    fun observeTermDetail(termId: String): Flow<DictionaryTermDetail?> =
        firestoreRepository.observeDictionaryTerms().map { remoteTerms ->
            mergeDetail(DictionaryContent.detailById(termId), remoteTerms[termId])
        }

    private fun mergeTerm(
        local: DictionaryTerm,
        remote: DictionaryTermDetail?,
    ): DictionaryTerm = local.copy(
        imageUrl = resolveImageUrl(local.id, remote?.imageUrl),
    )

    private fun mergeDetail(
        local: DictionaryTermDetail?,
        remote: DictionaryTermDetail?,
    ): DictionaryTermDetail? {
        local ?: return null
        if (remote == null) {
            return local.copy(imageUrl = resolveImageUrl(local.id, ""))
        }
        return local.copy(
            title = remote.title.ifBlank { local.title },
            description = remote.description.ifBlank { local.description },
            symptoms = remote.symptoms.ifBlank { local.symptoms },
            causes = remote.causes.ifBlank { local.causes },
            causesSectionTitle = remote.causesSectionTitle.ifBlank { local.causesSectionTitle },
            scanDescription = remote.scanDescription.ifBlank { local.scanDescription },
            recommendations = remote.recommendations.ifBlank { local.recommendations },
            imageUrl = resolveImageUrl(local.id, remote.imageUrl),
        )
    }

    private fun resolveImageUrl(termId: String, remoteUrl: String?): String =
        remoteUrl?.takeIf { it.isNotBlank() } ?: DictionaryContent.assetImagePath(termId)
}
