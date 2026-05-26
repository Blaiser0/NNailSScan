package com.example.nnailscan.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.NailClassifier
import com.example.nnailscan.R
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.firebase.FirestoreRepository
import com.example.nnailscan.navigation.ScanSessionState
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.util.formatClassificationLabel
import com.example.nnailscan.util.mapLabelToDictionaryTermId
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onNavigateToResult: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val firestoreRepository = remember { FirestoreRepository() }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val bitmap = loadBitmapFromUri(context.contentResolver, uri)
        if (bitmap == null) {
            Toast.makeText(context, R.string.error_image_load, Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }
        classifyAndNavigate(
            context = context,
            bitmap = bitmap,
            scope = scope,
            authRepository = authRepository,
            firestoreRepository = firestoreRepository,
            onNavigateToResult = onNavigateToResult,
        )
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap == null) {
            Toast.makeText(context, R.string.error_camera_cancelled, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        classifyAndNavigate(
            context = context,
            bitmap = bitmap,
            scope = scope,
            authRepository = authRepository,
            firestoreRepository = firestoreRepository,
            onNavigateToResult = onNavigateToResult,
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.scan_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(20.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.select_image),
            onClick = { pickImageLauncher.launch("image/*") },
        )

        Spacer(modifier = Modifier.height(12.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.take_photo),
            onClick = { takePictureLauncher.launch(null) },
        )
    }
}

private fun classifyAndNavigate(
    context: android.content.Context,
    bitmap: Bitmap,
    scope: kotlinx.coroutines.CoroutineScope,
    authRepository: AuthRepository,
    firestoreRepository: FirestoreRepository,
    onNavigateToResult: () -> Unit,
) {
    try {
        NailClassifier(context.applicationContext).use { classifier ->
            val (rawLabel, confidence) = classifier.classifyImage(bitmap)
            val formattedLabel = formatClassificationLabel(rawLabel)
            val dictionaryTermId = mapLabelToDictionaryTermId(rawLabel)

            ScanSessionState.current = ScanSessionState.Payload(
                bitmap = bitmap,
                rawLabel = rawLabel,
                formattedLabel = formattedLabel,
                confidence = confidence,
                dictionaryTermId = dictionaryTermId,
                scannedAtMillis = System.currentTimeMillis(),
            )

            saveScanToFirestore(
                scope = scope,
                authRepository = authRepository,
                firestoreRepository = firestoreRepository,
                resultLabel = formattedLabel,
            )

            onNavigateToResult()
        }
    } catch (error: Exception) {
        Toast.makeText(context, friendlyError(context, error), Toast.LENGTH_LONG).show()
    }
}

private fun saveScanToFirestore(
    scope: kotlinx.coroutines.CoroutineScope,
    authRepository: AuthRepository,
    firestoreRepository: FirestoreRepository,
    resultLabel: String,
) {
    val userId = authRepository.currentUser?.uid ?: return
    scope.launch {
        firestoreRepository.saveScan(userId, resultLabel)
    }
}

private fun loadBitmapFromUri(
    contentResolver: android.content.ContentResolver,
    uri: Uri,
): Bitmap? = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }

private fun friendlyError(context: android.content.Context, error: Exception): String {
    val message = error.message?.lowercase().orEmpty()
    return when {
        message.contains("nail_model") ||
            message.contains("tflite") ||
            message.contains("asset") ||
            error is java.io.FileNotFoundException ->
            context.getString(R.string.error_model_missing)

        message.contains("image") || message.contains("bitmap") ->
            context.getString(R.string.error_image_load)

        else -> context.getString(R.string.error_generic)
    }
}
