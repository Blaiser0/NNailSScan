package com.example.nnailscan.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

object ExternalIntents {
    fun sendEmail(
        context: Context,
        to: String,
        subject: String,
        body: String = "",
    ) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            if (body.isNotBlank()) {
                putExtra(Intent.EXTRA_TEXT, body)
            }
        }
        runCatching {
            context.startActivity(
                Intent.createChooser(intent, null),
            )
        }.onFailure {
            Toast.makeText(
                context,
                "No se encontró una app de correo instalada.",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    fun openPlayStore(context: Context, packageName: String) {
        val marketIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri(),
        )
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri(),
        )
        runCatching {
            context.startActivity(marketIntent)
        }.onFailure {
            runCatching {
                context.startActivity(webIntent)
            }.onFailure {
                Toast.makeText(
                    context,
                    "No se pudo abrir Google Play.",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
}
