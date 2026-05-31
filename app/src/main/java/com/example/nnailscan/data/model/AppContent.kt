package com.example.nnailscan.data.model

data class AboutAppSection(
    val title: String,
    val body: String,
)

object AppContent {
    const val APP_VERSION = "1.2.0"

    val termsAndConditions: String = """
        1. Aceptación de los términos
        Al registrarte y usar NailScan aceptas estos términos y condiciones. Si no estás de acuerdo, no utilices la aplicación.

        2. Naturaleza del servicio
        NailScan es una herramienta de apoyo basada en inteligencia artificial que analiza imágenes de uñas para identificar posibles patrones asociados a condiciones ungueales. No constituye un diagnóstico médico ni sustituye la consulta con un dermatólogo o médico calificado.

        3. Uso responsable
        Debes proporcionar información veraz al registrarte, usar imágenes propias o con autorización, y no utilizar la app con fines fraudulentos o para tomar decisiones médicas sin supervisión profesional.

        4. Cuenta de usuario
        Eres responsable de mantener la confidencialidad de tu contraseña y de toda actividad realizada desde tu cuenta. Puedes cerrar sesión en cualquier momento desde la sección Perfil.

        5. Historial de escaneos
        Los resultados de tus análisis se almacenan en tu cuenta para consulta personal. NailScan no garantiza la conservación indefinida de los datos; se recomienda conservar capturas relevantes si lo necesitas.

        6. Limitación de responsabilidad
        NailScan y sus desarrolladores no se hacen responsables de decisiones de salud tomadas exclusivamente con base en los resultados de la app. Ante cualquier duda o síntoma persistente, consulta a un profesional de salud.

        7. Modificaciones
        Podemos actualizar estos términos. El uso continuado de la app tras los cambios implica la aceptación de la versión vigente.

        8. Contacto
        Para consultas sobre estos términos escribe a soporte@nailscan.app.
    """.trimIndent()

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

        ¿Qué enfermedades detecta NailScan?
        El modelo identifica 8 condiciones: acropaquia, dedo azul, melanoma acral, onicogrifosis, onicomicosis, picaduras/trauma, psoriasis ungueal y uñas sanas.

        ¿Es un diagnóstico médico?
        No. NailScan es una herramienta informativa. Siempre consulta a un dermatólogo para confirmación clínica.

        ¿Por qué no funciona el escaneo?
        Verifica que el archivo nail_model_nuevo.tflite esté en assets, que tengas conexión a internet para sincronizar el historial y que la imagen tenga suficiente luz y nitidez.

        ¿Cómo recupero mi contraseña?
        En Iniciar sesión, pulsa «¿Olvidaste tu contraseña?», ingresa tu correo y abre el enlace de verificación en este dispositivo. La nueva contraseña se crea dentro de la app.

        Contacto
        Correo: soporte@nailscan.app
        Horario de respuesta: lunes a viernes, 9:00–18:00 (hora de España).
    """.trimIndent()

    val aboutMission: String = """
        NailScan es una aplicación móvil de salud ungueal que te ayuda a detectar posibles alteraciones en tus uñas mediante inteligencia artificial entrenada localmente en tu dispositivo.
    """.trimIndent()

    val aboutFeatures: String = """
        • Análisis instantáneo con cámara o galería
        • Modelo TFLite con 8 clases de condiciones ungueales
        • Diccionario educativo con descripción, síntomas y causas
        • Historial personal sincronizado con Firebase
        • Cuenta segura con autenticación por correo
    """.trimIndent()

    val aboutCredits: String = """
        Desarrollado por el equipo NNailSScan.
        Proyecto Firebase: nailscan-65b49
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
        NailScan es una aplicación móvil de salud ungueal desarrollada para ayudarte a detectar posibles alteraciones en tus uñas mediante inteligencia artificial.

        Características principales
        • Análisis instantáneo con cámara o galería
        • Modelo TFLite entrenado con 8 clases de condiciones ungueales
        • Diccionario educativo con descripción, síntomas y causas
        • Historial personal de escaneos sincronizado con Firebase
        • Cuenta segura con autenticación por correo

        Versión
        $APP_VERSION

        Desarrollado por el equipo NNailSScan.
        Proyecto Firebase: nailscan-65b49

        Recuerda: NailScan no reemplaza la atención médica profesional. Ante cualquier lesión sospechosa, especialmente bandas oscuras o cambios rápidos, consulta a un dermatólogo de inmediato.
    """.trimIndent()

    val feedback: String = """
        Tu opinión nos ayuda a mejorar NailScan.

        Calificar la app
        Si NailScan te resulta útil, déjanos una valoración en Google Play. Tus comentarios nos permiten priorizar mejoras en precisión del modelo, interfaz y nuevas funciones.

        Sugerir una afección
        ¿Hay alguna condición ungueal que te gustaría que el modelo detecte en el futuro? Escríbenos a feedback@nailscan.app indicando:
        • Nombre de la afección
        • Síntomas que observas
        • Por qué sería útil incluirla

        Reportar un error
        Si un escaneo arrojó un resultado incorrecto, envíanos (sin datos personales sensibles) una descripción del caso y, si es posible, capturas de pantalla del resultado.

        Contacto de retroalimentación
        feedback@nailscan.app
    """.trimIndent()
}
