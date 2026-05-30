package com.example.nnailscan.data.model

data class DictionaryTerm(
    val id: String,
    val title: String,
    val description: String,
)

data class DictionaryTermDetail(
    val id: String,
    val title: String,
    val description: String,
    val symptoms: String,
    val causes: String,
    val causesSectionTitle: String,
    val scanDescription: String,
    val recommendations: String,
)

object DictionaryContent {
    const val ACROPAQUIA = "acropaquia"
    const val DEDO_AZUL = "dedo_azul"
    const val MELANOMA = "melanoma_acral"
    const val ONICOGRIFOSIS = "onicogrifosis"
    const val ONICOMICOSIS = "onicomicosis"
    const val PICADURAS = "picaduras"
    const val PSORIASIS = "psoriasis_unas"
    const val UNA_SANA = "unas_sanas"

    val terms: List<DictionaryTerm> = listOf(
        DictionaryTerm(
            id = MELANOMA,
            title = "Melanoma Acral",
            description = "Lesión pigmentada maligna en la matriz o lecho ungueal.",
        ),
        DictionaryTerm(
            id = ONICOGRIFOSIS,
            title = "Onicogrifosis",
            description = "Engrosamiento y curvatura anormal de la lámina ungueal.",
        ),
        DictionaryTerm(
            id = ONICOMICOSIS,
            title = "Onicomicosis",
            description = "Infección fúngica que afecta color, grosor y textura de la uña.",
        ),
        DictionaryTerm(
            id = DEDO_AZUL,
            title = "Dedo Azul",
            description = "Cianosis o hematoma que oscurece la uña por falta de oxígeno o trauma.",
        ),
        DictionaryTerm(
            id = ACROPAQUIA,
            title = "Acropaquia",
            description = "Ensanchamiento de falanges distales con uñas en forma de reloj de arena.",
        ),
        DictionaryTerm(
            id = PSORIASIS,
            title = "Psoriasis Ungueal",
            description = "Alteraciones como pitting, manchas y engrosamiento por psoriasis.",
        ),
        DictionaryTerm(
            id = PICADURAS,
            title = "Picaduras / Trauma",
            description = "Cambios ungueales por mordeduras, golpes o lesiones repetidas.",
        ),
        DictionaryTerm(
            id = UNA_SANA,
            title = "Uña Sana",
            description = "Lámina ungueal sin signos evidentes de patología detectable.",
        ),
    )

    private val details: Map<String, DictionaryTermDetail> = mapOf(
        MELANOMA to DictionaryTermDetail(
            id = MELANOMA,
            title = "Melanoma Acral",
            description = "El melanoma acral es un tipo de melanoma que aparece en palmas, plantas y uñas. " +
                "En la uña puede manifestarse como una banda longitudinal oscura, mancha irregular o decoloración " +
                "que progresa hacia la cutícula (signo de Hutchinson).",
            symptoms = "Banda pigmentada oscura de bordes irregulares, cambio de color o tamaño, sangrado espontáneo, " +
                "deformidad progresiva de la uña o extensión del pigmento al tejido perioníquico.",
            causes = "Mutaciones en melanocitos, exposición UV acumulada, antecedentes familiares de melanoma, " +
                "piel clara con quemaduras previas y lesiones pigmentadas no evaluadas.",
            causesSectionTitle = "Causas y factores de riesgo",
            scanDescription = "El modelo de NailScan detectó patrones compatibles con melanoma acral: bandas oscuras, " +
                "pigmentación irregular o alteraciones profundas de la matriz ungueal.",
            recommendations = "Acude de inmediato a un dermatólogo para dermatoscopia y posible biopsia. " +
                "No retrases la evaluación si la lesión crece, sangra o cambia de aspecto.",
        ),
        ONICOGRIFOSIS to DictionaryTermDetail(
            id = ONICOGRIFOSIS,
            title = "Onicogrifosis",
            description = "Consiste en el engrosamiento excesivo de la uña (hiperqueratosis) con curvatura " +
                "anormal, a menudo en el dedo gordo del pie. Puede causar dolor al calzado y dificultad para cortar la uña.",
            symptoms = "Uña muy gruesa, amarillenta o marrón, bordes elevados, curvatura en garra, " +
                "dolor a la presión y posible inflamación perioníquica.",
            causes = "Traumatismos repetidos, calzado ajustado, edad avanzada, mala higiene, diabetes, " +
                "problemas circulatorios o infecciones crónicas no tratadas.",
            causesSectionTitle = "Causas",
            scanDescription = "NailScan identificó engrosamiento y deformidad de la lámina ungueal compatibles con onicogrifosis.",
            recommendations = "Mantén las uñas limpias y secas, usa calzado amplio y consulta a un podólogo o dermatólogo " +
                "para corte profesional y descartar otras causas subyacentes.",
        ),
        ONICOMICOSIS to DictionaryTermDetail(
            id = ONICOMICOSIS,
            title = "Onicomicosis",
            description = "Infección fúngica de la uña causada por dermatofitos, levaduras o mohos. " +
                "Altera el color (blanco, amarillo o marrón), el grosor y puede provocar fragilidad o desprendimiento.",
            symptoms = "Decoloración amarillenta o blanca, engrosamiento, textura quebradiza, separación de la uña " +
                "del lecho (onixis) y mal olor en casos avanzados.",
            causes = "Ambientes húmedos (piscinas, duchas compartidas), sudoración excesiva, calzado cerrado, " +
                "microlesiones, diabetes o sistema inmune debilitado.",
            causesSectionTitle = "Causas",
            scanDescription = "El análisis sugiere cambios de color y textura propios de una posible onicomicosis.",
            recommendations = "Evita humedad prolongada, no compartas cortaúñas y consulta a un dermatólogo para confirmar " +
                "el diagnóstico con cultivo o examen directo antes de iniciar tratamiento antifúngico.",
        ),
        DEDO_AZUL to DictionaryTermDetail(
            id = DEDO_AZUL,
            title = "Dedo Azul",
            description = "La coloración azulada o violácea puede deberse a cianosis (baja oxigenación), " +
                "hematoma subungueal tras un golpe o enfermedades vasculares que afectan la circulación distal.",
            symptoms = "Uña o dedo azulados, sensación de frío, dolor tras trauma, hinchazón o cambio brusco de color " +
                "después de un impacto.",
            causes = "Golpes directos, enfermedad de Raynaud, problemas cardiopulmonares, vasculitis, " +
                "exposición al frío extremo o medicamentos que afectan la circulación.",
            causesSectionTitle = "Causas",
            scanDescription = "NailScan detectó tonalidades azuladas o oscuras compatibles con cianosis o hematoma subungueal.",
            recommendations = "Si apareció tras un golpe, aplica hielo las primeras 24 h. Si el color persiste, empeora " +
                "o hay dolor intenso, consulta a un médico para descartar problemas circulatorios.",
        ),
        ACROPAQUIA to DictionaryTermDetail(
            id = ACROPAQUIA,
            title = "Acropaquia",
            description = "También llamada uñas en palillo de tambor. Los dedos se ensanchan distalmente y la uña " +
                "adopta una curvatura aumentada. Puede asociarse a enfermedades pulmonares, cardíacas u otras sistémicas.",
            symptoms = "Ensanchamiento de la falange distal, uñas muy curvas, aumento del ángulo ungueal (>180°), " +
                "consistencia esponjosa al presionar la base del dedo.",
            causes = "Enfermedad pulmonar crónica, cardiopatías congénitas, cáncer de pulmón, enfermedad inflamatoria " +
                "intestinal, infecciones crónicas o, en algunos casos, es idiopática.",
            causesSectionTitle = "Causas y factores de riesgo",
            scanDescription = "El modelo identificó un patrón de uña curva y ensanchada compatible con acropaquia.",
            recommendations = "La acropaquia puede reflejar una condición sistémica. Programa una evaluación médica " +
                "completa (pulmonar y cardiovascular) si notas estos cambios progresivos.",
        ),
        PSORIASIS to DictionaryTermDetail(
            id = PSORIASIS,
            title = "Psoriasis Ungueal",
            description = "La psoriasis puede afectar las uñas produciendo pitting (pequeñas depresiones), manchas " +
                "aceitosa, engrosamiento subungueal y separación de la lámina del lecho.",
            symptoms = "Pitting ungueal, manchas amarillentas o marrones, engrosamiento, fragilidad, dolor " +
                "y onixis parcial en dedos con lesiones cutáneas de psoriasis.",
            causes = "Psoriasis cutánea preexistente, predisposición genética, estrés, traumatismos ungueales " +
                "(fenómeno de Koebner), infecciones o ciertos medicamentos.",
            causesSectionTitle = "Causas",
            scanDescription = "NailScan detectó depresiones, manchas o irregularidades típicas de psoriasis ungueal.",
            recommendations = "Consulta a un dermatólogo para confirmar el diagnóstico y valorar tratamiento tópico, " +
                "sistémico o biológico según la extensión de la enfermedad.",
        ),
        PICADURAS to DictionaryTermDetail(
            id = PICADURAS,
            title = "Picaduras / Trauma",
            description = "Lesiones mecánicas como mordeduras de uñas (onicofagia), golpes, roce con calzado o " +
                "manipulación repetida pueden alterar temporalmente la forma y color de la uña.",
            symptoms = "Bordes irregulares, hematomas, surcos transversales, inflamación perioníquica, " +
                "dolor localizado y crecimiento ungueal irregular.",
            causes = "Mordisqueo de uñas, golpes directos, deportes de contacto, calzado inadecuado, " +
                "manipulación con herramientas o hábitos repetitivos.",
            causesSectionTitle = "Causas",
            scanDescription = "El análisis muestra signos compatibles con trauma o daño mecánico reciente en la uña.",
            recommendations = "Protege la uña de nuevos traumatismos, mantén las uñas cortas y limpias. " +
                "Si hay dolor intenso, supuración o no mejora en semanas, acude a un profesional de salud.",
        ),
        UNA_SANA to DictionaryTermDetail(
            id = UNA_SANA,
            title = "Uña Sana",
            description = "Una uña sana presenta color rosado uniforme, superficie lisa, bordes regulares, " +
                "lunula visible y crecimiento constante sin manchas ni deformidades evidentes.",
            symptoms = "Coloración uniforme, superficie lisa, sin dolor, sin separación del lecho ungueal " +
                "y sin bandas ni manchas persistentes.",
            causes = "Buena nutrición, higiene adecuada, ausencia de trauma recurrente y salud general estable " +
                "favorecen uñas sanas.",
            causesSectionTitle = "Buenas prácticas",
            scanDescription = "NailScan no detectó patrones significativos de enfermedad ungueal en esta imagen.",
            recommendations = "Continúa con higiene regular, evita humedad prolongada, usa guantes para tareas agresivas " +
                "y realiza escaneos periódicos si notas algún cambio.",
        ),
    )

    fun termById(id: String): DictionaryTerm? = terms.find { it.id == id }

    fun detailById(id: String): DictionaryTermDetail? = details[id]

    fun detailByLabel(rawLabel: String): DictionaryTermDetail? =
        details[rawLabel.lowercase()] ?: detailById(rawLabel)
}
