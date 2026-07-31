package com.example.nnailscan.util

import com.example.nnailscan.data.model.ScanRecord

fun ScanRecord.resolveUserDisplayName(userNamesById: Map<String, String> = emptyMap()): String =
    userFullName.ifBlank { userNamesById[userId].orEmpty() }.ifBlank { "Usuario desconocido" }
