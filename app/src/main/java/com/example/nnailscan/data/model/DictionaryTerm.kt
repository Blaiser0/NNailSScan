package com.example.nnailscan.data.model

data class DictionaryTerm(
    val id: String,
    val title: String,
    val description: String,
)

data class DictionaryTermDetail(
    val id: String,
    val title: String,
    val causesSectionTitle: String,
)

object DictionaryContent {
    const val MELANOMA = "melanoma_lentiginoso_acral"
    const val ONICOGRIFOSIS = "onicogrifosis"
    const val DEDO_AZUL = "dedo_azul"
    const val ACROPAQUIA = "acropaquia"
    const val PITTING = "pitting_ungueal"
    const val UNA_SANA = "una_sana"

    val terms: List<DictionaryTerm> = listOf(
        DictionaryTerm(
            id = MELANOMA,
            title = "Melanoma Lentiginoso Acral",
            description = "Tipo de cáncer de piel que aparece en las uñas",
        ),
        DictionaryTerm(
            id = ONICOGRIFOSIS,
            title = "Onicogrifosis",
            description = "Engrosamiento y curvatura anormal de la uña",
        ),
        DictionaryTerm(
            id = DEDO_AZUL,
            title = "Dedo Azul",
            description = "Coloración azulada por falta de oxígeno",
        ),
        DictionaryTerm(
            id = ACROPAQUIA,
            title = "Acropaquia",
            description = "Ensanchamiento de los dedos y uñas curvas",
        ),
        DictionaryTerm(
            id = PITTING,
            title = "Pitting Ungueal",
            description = "Pequeñas depresiones en la superficie de la uña",
        ),
        DictionaryTerm(
            id = UNA_SANA,
            title = "Uña Sana",
            description = "Uña sin anomalías o condiciones detectables",
        ),
    )

    private val details: Map<String, DictionaryTermDetail> = mapOf(
        MELANOMA to DictionaryTermDetail(
            id = MELANOMA,
            title = "Melanoma Lentiginoso Acral",
            causesSectionTitle = "Causas y factores de riesgo",
        ),
        ONICOGRIFOSIS to DictionaryTermDetail(
            id = ONICOGRIFOSIS,
            title = "Onicogrifosis",
            causesSectionTitle = "Causas",
        ),
        DEDO_AZUL to DictionaryTermDetail(
            id = DEDO_AZUL,
            title = "Dedo Azul",
            causesSectionTitle = "Causas",
        ),
        ACROPAQUIA to DictionaryTermDetail(
            id = ACROPAQUIA,
            title = "Acropaquia",
            causesSectionTitle = "Causas y factores de riesgo",
        ),
        PITTING to DictionaryTermDetail(
            id = PITTING,
            title = "Pitting Ungueal",
            causesSectionTitle = "Causas",
        ),
        UNA_SANA to DictionaryTermDetail(
            id = UNA_SANA,
            title = "Uña Sana",
            causesSectionTitle = "Causas",
        ),
    )

    fun termById(id: String): DictionaryTerm? = terms.find { it.id == id }

    fun detailById(id: String): DictionaryTermDetail? = details[id]
}
