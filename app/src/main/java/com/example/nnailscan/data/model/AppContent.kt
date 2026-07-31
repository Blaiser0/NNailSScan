package com.example.nnailscan.data.model

data class AboutAppSection(
    val title: String,
    val body: String,
)

object AppContent {
    const val APP_VERSION = "1.2.0"

    val termsSections: List<AboutAppSection> = listOf(
        AboutAppSection(
            title = "1. Aceptación de los términos",
            body = """
                Al crear tu cuenta y usar la app, aceptas estos términos. Si no estás de acuerdo, te pedimos que no utilices NailScan.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "2. Naturaleza del servicio",
            body = """
                NailScan te ayuda a revisar tus uñas a partir de una foto. Los resultados son orientativos y no reemplazan una consulta con un dermatólogo u otro profesional de salud.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "3. Uso responsable",
            body = """
                Usa información real al registrarte, sube fotos tuyas o con permiso de la persona, y no utilices NailScan para engañar a otros ni para tomar decisiones médicas sin asesoramiento profesional.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "4. Cuenta de usuario",
            body = """
                Cuida tu contraseña y de lo que se haga desde tu cuenta. Puedes cerrar sesión cuando quieras desde la sección Perfil.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "5. Historial de escaneos",
            body = """
                Guardamos tus escaneos en tu cuenta para que puedas consultarlos. Te recomendamos guardar capturas importantes por si las necesitas más adelante.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "6. Limitación de responsabilidad",
            body = """
                NailScan no se hace responsable de decisiones de salud basadas solo en los resultados de la app. Si tienes dudas o síntomas que persisten, consulta a un profesional.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "7. Modificaciones",
            body = """
                Podemos actualizar estos términos. Si sigues usando la app después de un cambio, se entiende que aceptas la versión actualizada.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "8. Contacto",
            body = """
                Si tienes preguntas sobre estos términos, escríbenos a:
                Correo: 22221039@unamad.edu.pe
            """.trimIndent(),
        ),
    )

    val termsAndConditions: String = termsSections.joinToString(separator = "\n\n") { section ->
        "${section.title}\n${section.body}"
    }

    val privacyPolicy: String = """
        1. Responsable del tratamiento
        NailScan trata tus datos personales conforme a la normativa aplicable de protección de datos. El responsable es el equipo de desarrollo de NailScan.

        2. Datos que recopilamos
        • Datos de registro: nombre, correo electrónico y contraseña (almacenada de forma segura por Firebase Authentication).
        • Datos de uso: historial de escaneos, resultados de clasificación y fecha de cada análisis.
        • Imágenes: las fotografías de uñas se procesan para el análisis de IA y pueden almacenarse asociadas a tu cuenta.

        3. Finalidad del tratamiento
        Autenticar tu cuenta, guardar tu historial de escaneos, mejorar la precisión del modelo y ofrecer soporte técnico cuando lo solicites.

        4. Base legal
        Tu consentimiento al registrarte y aceptar la política de privacidad, así como la ejecución del servicio contratado al usar la aplicación.

        5. Almacenamiento y seguridad
        Los datos se alojan en Firebase (Google Cloud) con medidas de seguridad estándar de la industria. No vendemos ni compartimos tus datos con terceros con fines comerciales.

        6. Tus derechos
        Puedes solicitar acceso, rectificación o eliminación de tus datos contactando a soporte@nailscan.app. También puedes cerrar sesión y dejar de usar la app en cualquier momento.

        7. Conservación
        Conservamos tus datos mientras mantengas una cuenta activa. Tras la eliminación de la cuenta, los datos se borrarán en un plazo razonable salvo obligación legal de conservación.

        8. Cambios en esta política
        Publicaremos actualizaciones en la app. Te recomendamos revisarla periódicamente.
    """.trimIndent()

    val technicalSupport: String = """
        Preguntas frecuentes

        ¿Cómo escaneo una uña?
        Desde Inicio, pulsa el botón de cámara, toma una foto con buena iluminación o elige una imagen de la galería. Mantén la uña centrada y enfocada.

        ¿Es un diagnóstico médico?
        No. NailScan es una herramienta informativa. Siempre consulta a un dermatólogo para confirmación clínica.

        ¿Cómo recupero mi contraseña?
        En Iniciar sesión, pulsa «¿Olvidaste tu contraseña?», ingresa tu correo y abre el enlace de verificación en este dispositivo.

        Soporte
        NailScan ofrece ayuda para dudas sobre el uso de la app, problemas al escanear, tu cuenta o la sincronización del historial. Los resultados son orientativos y no sustituyen una consulta médica.

        Contacto
        Correo: 22221039@unamad.edu.pe
    """.trimIndent()

    val aboutMission: String = """
        NailScan te ayuda a revisar el estado de tus uñas desde el celular. Toma una foto y recibe una orientación sobre posibles alteraciones ungueales para que puedas decidir si conviene consultar a un especialista.
    """.trimIndent()

    val aboutFeatures: String = """
        • Escanea tus uñas con la cámara o una foto de la galería
        • Conoce posibles condiciones ungueales de forma clara
        • Consulta el diccionario con descripción, síntomas y causas
        • Revisa tu historial de escaneos cuando lo necesites
        • Guarda tu información con una cuenta personal
    """.trimIndent()

    val aboutCredits: String = """
        Desarrollado por un equipo de 3 estudiantes de la carrera de Ing. Sistemas e Informatica
    """.trimIndent()

    val aboutDisclaimer: String = """
        NailScan no reemplaza la atención médica profesional. Ante cualquier lesión sospechosa, especialmente bandas oscuras o cambios rápidos, consulta a un dermatólogo de inmediato.
    """.trimIndent()

    val aboutSections: List<AboutAppSection> = listOf(
        AboutAppSection(title = "Nuestra misión", body = aboutMission),
        AboutAppSection(title = "Características principales", body = aboutFeatures),
        AboutAppSection(title = "Versión", body = APP_VERSION),
        AboutAppSection(title = "Créditos", body = aboutCredits),
        AboutAppSection(title = "Aviso importante", body = aboutDisclaimer),
    )

    val aboutApp: String = """
        NailScan te ayuda a revisar el estado de tus uñas desde el celular. Toma una foto y recibe una orientación sobre posibles alteraciones ungueales.

        Características principales
        • Escanea tus uñas con la cámara o una foto de la galería
        • Conoce posibles condiciones ungueales de forma clara
        • Consulta el diccionario con descripción, síntomas y causas
        • Revisa tu historial de escaneos cuando lo necesites
        • Guarda tu información con una cuenta personal

        Versión
        $APP_VERSION

        Desarrollado por un equipo de 3 estudiantes de la carrera de Ing. Sistemas e Informatica

        Recuerda: NailScan no reemplaza la atención médica profesional. Ante cualquier lesión sospechosa, especialmente bandas oscuras o cambios rápidos, consulta a un dermatólogo de inmediato.
    """.trimIndent()

    val feedbackSections: List<AboutAppSection> = listOf(
        AboutAppSection(
            title = "Tu opinión importa",
            body = """
                Queremos que NailScan sea más útil para ti. Aquí puedes contarnos qué te gustaría mejorar, qué afección te gustaría que reconozca la app o si algo no funcionó como esperabas.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "Sugerir una afección",
            body = """
                ¿Hay alguna condición de las uñas que te gustaría que NailScan pueda identificar? Escríbenos indicando el nombre de la afección, los síntomas que sueles notar y por qué crees que sería útil incluirla.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "Reportar un problema",
            body = """
                Si un escaneo te dio un resultado que no coincide con lo que ves, cuéntanos qué pasó. Puedes incluir una breve descripción y, si lo deseas, una captura de pantalla del resultado.
            """.trimIndent(),
        ),
        AboutAppSection(
            title = "Contacto",
            body = """
                Para enviarnos sugerencias o reportes, escríbenos a:
                Correo: 22221039@unamad.edu.pe
            """.trimIndent(),
        ),
    )

    val feedback: String = feedbackSections.joinToString(separator = "\n\n") { section ->
        "${section.title}\n${section.body}"
    }
}
