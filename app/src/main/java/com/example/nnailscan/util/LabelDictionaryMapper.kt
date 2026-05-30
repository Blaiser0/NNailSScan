package com.example.nnailscan.util

import com.example.nnailscan.data.model.DictionaryContent

fun formatClassificationLabel(rawLabel: String): String =
    DictionaryContent.detailByLabel(rawLabel)?.title
        ?: rawLabel
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { word ->
                word.replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
            }

fun mapLabelToDictionaryTermId(rawLabel: String): String {
    val normalized = rawLabel.lowercase()
    return DictionaryContent.terms
        .firstOrNull { it.id == normalized }
        ?.id
        ?: DictionaryContent.UNA_SANA
}
