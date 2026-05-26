package com.example.nnailscan.util

import com.example.nnailscan.data.model.DictionaryContent

fun formatClassificationLabel(rawLabel: String): String =
    rawLabel
        .replace('_', ' ')
        .split(' ')
        .joinToString(" ") { word ->
            word.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }

fun mapLabelToDictionaryTermId(rawLabel: String): String =
    when (rawLabel.lowercase()) {
        "melanoma_acral" -> DictionaryContent.MELANOMA
        "onicogrifosis" -> DictionaryContent.ONICOGRIFOSIS
        "dedo_azul" -> DictionaryContent.DEDO_AZUL
        "acropaquia" -> DictionaryContent.ACROPAQUIA
        "onicomicosis" -> DictionaryContent.ONICOGRIFOSIS
        "picaduras" -> DictionaryContent.DEDO_AZUL
        "psoriasis_unas" -> DictionaryContent.PITTING
        "unas_sanas" -> DictionaryContent.UNA_SANA
        else -> DictionaryContent.terms
            .firstOrNull { term ->
                term.title.equals(formatClassificationLabel(rawLabel), ignoreCase = true)
            }
            ?.id ?: DictionaryContent.UNA_SANA
    }
