package com.example.nnailscan.navigation

import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object PasswordResetLinkHandler {
    private val _oobCodeEvents = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val oobCodeEvents = _oobCodeEvents.asSharedFlow()

    fun handle(intent: Intent?) {
        val link = intent?.data ?: return
        if (!isPasswordResetLink(link)) return

        val oobCode = link.getQueryParameter("oobCode") ?: return
        PasswordResetState.oobCode = oobCode
        PasswordResetState.isEmailVerified = false
        PasswordResetState.verifiedEmail = null
        _oobCodeEvents.tryEmit(oobCode)
    }

    private fun isPasswordResetLink(link: Uri): Boolean {
        if (link.getQueryParameter("mode") != "resetPassword") return false

        val host = link.host.orEmpty()
        val scheme = link.scheme.orEmpty()
        return when {
            scheme == "https" && host.endsWith("firebaseapp.com") -> true
            scheme == "nnailscan" && host == "auth" -> true
            else -> false
        }
    }
}
