package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.ui.components.MedicalDisclaimerCard
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.TermDetailInfoCard
import com.example.nnailscan.ui.components.TermImagePlaceholder
import com.example.nnailscan.ui.theme.NailScanBackground

@Composable
fun TermDetailScreen(
    termId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val detail = DictionaryContent.detailById(termId)
        ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            NailScanScreenHeader(
                title = detail.title,
                onBack = onBack,
            )
        }

        item {
            TermImagePlaceholder()
        }

        item {
            TermDetailInfoCard(
                title = stringResource(R.string.term_detail_description),
                lineCount = 6,
            )
        }

        item {
            TermDetailInfoCard(
                title = stringResource(R.string.term_detail_symptoms),
                lineCount = 5,
            )
        }

        item {
            TermDetailInfoCard(
                title = detail.causesSectionTitle,
                lineCount = 5,
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            MedicalDisclaimerCard()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
