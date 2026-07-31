package com.example.nnailscan.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.nnailscan.R
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanBorder
import com.example.nnailscan.ui.theme.NailScanDiagnosisBorder
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun ScanAnalyzedImageContainer(
    bitmap: Bitmap?,
    imageUrl: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(NailScanSurface)
            .border(1.dp, NailScanBorder, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center,
    ) {
        when {
            bitmap != null -> {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.image_preview_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
            }

            !imageUrl.isNullOrBlank() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = stringResource(R.string.image_preview_description),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
fun ScanDiagnosisCard(
    dateLabel: String,
    detectedDisease: String,
    confidence: Float,
    modifier: Modifier = Modifier,
) {
    val confidenceFraction = (confidence / 100f).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.5.dp, NailScanDiagnosisBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NailScanSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.scan_result_diagnosis_title),
                style = Typography.bodyLarge.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = dateLabel,
                style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(R.string.scan_result_detected_label))
                    }
                    append(" ")
                    append(detectedDisease)
                },
                style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.scan_result_confidence_label),
                    style = Typography.bodyMedium.copy(
                        color = NailScanTextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.scan_result_confidence_value, confidence),
                    style = Typography.bodyMedium.copy(
                        color = NailScanAccent,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { confidenceFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = NailScanAccent,
                trackColor = NailScanBorder,
            )
        }
    }
}

@Composable
fun ScanResultInfoCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NailScanSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = title,
                style = Typography.bodyLarge.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            ContentTextBlock(text = body)
        }
    }
}
