package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.ui.components.MedicalDisclaimerCard
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.TermConditionHeader
import com.example.nnailscan.ui.components.TermDetailInfoCard
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.viewmodel.DictionaryViewModel

@Composable
fun TermDetailScreen(
    termId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DictionaryViewModel = viewModel(),
) {
    val detail by viewModel.detailUiState(termId).collectAsState()
    val termDetail = detail ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            NailScanScreenHeader(
                title = termDetail.title,
                onBack = onBack,
            )
        }

        item {
            TermConditionHeader(
                termId = termDetail.id,
                title = termDetail.title,
                imageUrl = termDetail.imageUrl,
            )
        }

        item {
            TermDetailInfoCard(
                title = stringResource(R.string.term_detail_description),
                body = termDetail.description,
            )
        }

        item {
            TermDetailInfoCard(
                title = stringResource(R.string.term_detail_symptoms),
                body = termDetail.symptoms,
            )
        }

        item {
            TermDetailInfoCard(
                title = termDetail.causesSectionTitle,
                body = termDetail.causes,
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            MedicalDisclaimerCard()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
