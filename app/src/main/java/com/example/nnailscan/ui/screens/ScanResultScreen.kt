package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.navigation.ScanSessionState
import com.example.nnailscan.ui.components.MedicalDisclaimerCard
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.ScanAnalyzedImageContainer
import com.example.nnailscan.ui.components.ScanDiagnosisCard
import com.example.nnailscan.ui.components.ScanResultInfoCard
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.util.formatResultDate

@Composable
fun ScanResultScreen(
    onBack: () -> Unit,
    onLearnMore: (dictionaryTermId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val payload = ScanSessionState.current

    LaunchedEffect(payload) {
        if (payload == null) {
            onBack()
        }
    }

    if (payload == null) return

    val detail = DictionaryContent.detailById(payload.dictionaryTermId)
        ?: DictionaryContent.detailByLabel(payload.rawLabel)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            NailScanScreenHeader(
                title = stringResource(R.string.scan_result_title),
                onBack = onBack,
            )
        }

        item {
            ScanAnalyzedImageContainer(
                bitmap = payload.bitmap,
                imageUrl = payload.imageUrl,
            )
        }

        item {
            ScanDiagnosisCard(
                dateLabel = formatResultDate(payload.scannedAtMillis),
                detectedDisease = payload.formattedLabel,
                confidence = payload.confidence,
            )
        }

        item {
            ScanResultInfoCard(
                title = stringResource(R.string.scan_result_description_title),
                body = detail?.scanDescription
                    ?: stringResource(R.string.scan_result_default_description),
            )
        }

        item {
            ScanResultInfoCard(
                title = stringResource(R.string.scan_result_recommendations_title),
                body = detail?.recommendations
                    ?: stringResource(R.string.scan_result_default_recommendations),
            )
        }

        item {
            MedicalDisclaimerCard()
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            NailScanPrimaryButton(
                text = stringResource(R.string.scan_result_learn_more),
                onClick = { onLearnMore(payload.dictionaryTermId) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
