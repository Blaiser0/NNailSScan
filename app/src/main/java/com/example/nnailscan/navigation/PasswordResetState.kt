package com.example.nnailscan.navigation

object PasswordResetState {
    var email: String = ""
    var oobCode: String? = null
    var isEmailVerified: Boolean = false
    var verifiedEmail: String? = null
    var showLoginSuccessMessage: Boolean = false

    fun clear() {
        email = ""
        oobCode = null
        isEmailVerified = false
        verifiedEmail = null
    }

    fun clearAll() {
        clear()
        showLoginSuccessMessage = false
    }
}
