package com.example.nnailscan.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Punto central de acceso a servicios Firebase.
 *
 * Reemplaza [app/google-services.json] con el archivo descargado desde
 * Firebase Console (proyecto → Configuración → Tus apps → Android).
 * Habilita Authentication (Email/Password) y Cloud Firestore en la consola.
 */
object FirebaseConfig {
    const val USERS_COLLECTION = "users"
    const val SCANS_COLLECTION = "scans"

    val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()
}
