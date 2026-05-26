package com.example.nnailscan.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatScanDate(timestamp: Timestamp?): String {
    if (timestamp == null) return "Fecha: —"
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("d 'de' MMMM", Locale.forLanguageTag("es-ES"))
    return "Fecha: ${formatter.format(date)}"
}

fun formatScanResult(result: String): String =
    "Resultado: ${result.ifBlank { "Sin resultado" }}"

fun formatResultDate(millis: Long): String {
    val formatter = SimpleDateFormat("d 'de' MMMM", Locale.forLanguageTag("es-ES"))
    return "Fecha: ${formatter.format(Date(millis))}"
}
