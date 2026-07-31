package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.data.model.ScanRecord
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.ScanHistoryCard
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.HistoryViewModel
import com.example.nnailscan.util.formatScanDate
import com.example.nnailscan.util.formatScanResult
import com.example.nnailscan.util.resolveUserDisplayName

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onScanClick: (ScanRecord) -> Unit = {},
    isAdminViewMode: Boolean = false,
    viewModel: HistoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isAdminViewMode) {
        viewModel.bindAdminViewMode(isAdminViewMode)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        if (onBack != null) {
            NailScanScreenHeader(
                title = stringResource(
                    if (isAdminViewMode) R.string.history_title_all_users else R.string.history_title,
                ),
                onBack = onBack,
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            Text(
                text = stringResource(
                    if (isAdminViewMode) R.string.history_title_all_users else R.string.history_title,
                ),
                style = Typography.titleMedium.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (uiState.scans.isEmpty()) {
            Text(
                text = stringResource(R.string.history_empty),
                modifier = Modifier.fillMaxWidth(),
                style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.scans.forEach { scan ->
                    ScanHistoryCard(
                        dateLabel = formatScanDate(scan.createdAt),
                        result = formatScanResult(scan.result),
                        imageUrl = scan.imageUrl,
                        userName = if (isAdminViewMode) {
                            scan.resolveUserDisplayName(uiState.userNamesById)
                        } else {
                            null
                        },
                        onClick = { onScanClick(scan) },
                    )
                }
            }
        }
    }
}
