package com.example.nnailscan.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nnailscan.NailClassifier
import com.example.nnailscan.R
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun ScanScreen() {
    val context = LocalContext.current
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resultText by remember { mutableStateOf(context.getString(R.string.result_placeholder)) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val bitmap = loadBitmapFromUri(context.contentResolver, uri)
        if (bitmap == null) {
            Toast.makeText(context, R.string.error_image_load, Toast.LENGTH_LONG).show()
            return@rememberLauncherForActivityResult
        }
        classifyBitmap(context, bitmap) { label, confidence ->
            previewBitmap = bitmap
            resultText = context.getString(R.string.result_format, label, confidence)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        if (bitmap == null) {
            Toast.makeText(context, R.string.error_camera_cancelled, Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        classifyBitmap(context, bitmap) { label, confidence ->
            previewBitmap = bitmap
            resultText = context.getString(R.string.result_format, label, confidence)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = stringResource(R.string.scan_title),
            style = Typography.titleMedium.copy(
                color = NailScanTextPrimary,
                fontWeight = FontWeight.Bold,
            ),
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

        previewBitmap?.let { bitmap ->
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.image_preview_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = resultText,
            style = Typography.bodyLarge.copy(color = NailScanTextPrimary),
        )
    }
}

private fun loadBitmapFromUri(
    contentResolver: android.content.ContentResolver,
    uri: Uri,
): Bitmap? = contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }

private fun classifyBitmap(
    context: android.content.Context,
    bitmap: Bitmap,
    onSuccess: (label: String, confidence: Float) -> Unit,
) {
    try {
        NailClassifier(context.applicationContext).use { classifier ->
            val (rawLabel, confidence) = classifier.classifyImage(bitmap)
            onSuccess(formatLabel(rawLabel), confidence)
        }
    } catch (error: Exception) {
        Toast.makeText(context, friendlyError(context, error), Toast.LENGTH_LONG).show()
    }
}

private fun formatLabel(rawLabel: String): String =
    rawLabel
        .replace('_', ' ')
        .split(' ')
        .joinToString(" ") { word ->
            word.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }

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
