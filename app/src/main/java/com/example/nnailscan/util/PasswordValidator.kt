package com.example.nnailscan.util

object PasswordValidator {
    const val REQUIREMENTS_MESSAGE =
        "La contraseña debe tener al menos 8 caracteres, incluyendo 1 mayúscula, 1 minúscula, 1 número y 1 símbolo."

    fun validate(password: String): String? {
        if (password.length < 8) return REQUIREMENTS_MESSAGE
        if (!password.any { it.isUpperCase() }) return REQUIREMENTS_MESSAGE
        if (!password.any { it.isLowerCase() }) return REQUIREMENTS_MESSAGE
        if (!password.any { it.isDigit() }) return REQUIREMENTS_MESSAGE
        if (!password.any { !it.isLetterOrDigit() }) return REQUIREMENTS_MESSAGE
        return null
    }
}
