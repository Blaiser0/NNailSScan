package com.example.nnailscan.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.firebase.ScanRepository
import com.example.nnailscan.navigation.ScanSessionState
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanBackground
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onNavigateToResult: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val scanRepository = remember { ScanRepository() }
    var isProcessing by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val bitmap = loadBitmapFromUri(context.contentResolver, uri)
        if (bitmap == null) {
            Toast.makeText(context, R.string.error_image_load, Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }
        processScan(
            context = context,
            bitmap = bitmap,
            scope = scope,
            authRepository = authRepository,
            scanRepository = scanRepository,
            onProcessingChanged = { isProcessing = it },
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
        processScan(
            context = context,
            bitmap = bitmap,
            scope = scope,
            authRepository = authRepository,
            scanRepository = scanRepository,
            onProcessingChanged = { isProcessing = it },
            onNavigateToResult = onNavigateToResult,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NailScanBackground),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                enabled = !isProcessing,
            )

            Spacer(modifier = Modifier.height(12.dp))

            NailScanPrimaryButton(
                text = stringResource(R.string.take_photo),
                onClick = { takePictureLauncher.launch(null) },
                enabled = !isProcessing,
            )
        }

        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NailScanBackground.copy(alpha = 0.72f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = NailScanAccent)
            }
        }
    }
}

private fun processScan(
    context: android.content.Context,
    bitmap: Bitmap,
    scope: kotlinx.coroutines.CoroutineScope,
    authRepository: AuthRepository,
    scanRepository: ScanRepository,
    onProcessingChanged: (Boolean) -> Unit,
    onNavigateToResult: () -> Unit,
) {
    val userId = authRepository.currentUser?.uid
    if (userId == null) {
        Toast.makeText(context, R.string.error_not_authenticated, Toast.LENGTH_LONG).show()
        return
    }

    scope.launch {
        onProcessingChanged(true)
        val result = scanRepository.processAndPersistScan(
            context = context,
            userId = userId,
            bitmap = bitmap,
        )
        onProcessingChanged(false)

        result.fold(
            onSuccess = { payload ->
                ScanSessionState.current = payload
                onNavigateToResult()
            },
            onFailure = { error ->
                Toast.makeText(
                    context,
                    friendlyError(context, error),
                    Toast.LENGTH_LONG,
                ).show()
            },
        )
    }
}

private fun loadBitmapFromUri(
    contentResolver: android.content.ContentResolver,
    uri: Uri,
): Bitmap? = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }

private fun friendlyError(context: android.content.Context, error: Throwable): String {
    val message = error.message?.lowercase().orEmpty()
    return when {
        message.contains("nail_model") ||
            message.contains("tflite") ||
            message.contains("asset") ||
            error is java.io.FileNotFoundException ->
            context.getString(R.string.error_model_missing)

        message.contains("object does not exist") ||
            message.contains("storage no está configurado") ->
            context.getString(R.string.error_storage_not_configured)

        message.contains("storage") || message.contains("upload") ||
            message.contains("guardar la imagen") ->
            context.getString(R.string.error_image_upload)

        message.contains("image") || message.contains("bitmap") ->
            context.getString(R.string.error_image_load)

        else -> error.message ?: context.getString(R.string.error_generic)
    }
}
