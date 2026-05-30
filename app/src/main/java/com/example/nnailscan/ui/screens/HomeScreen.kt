package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.ui.components.RecentActivityCard
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanLink
import com.example.nnailscan.ui.theme.NailScanLogoCircle
import com.example.nnailscan.ui.theme.NailScanScanButton
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.HomeViewModel
import com.example.nnailscan.util.formatScanDate
import com.example.nnailscan.util.formatScanResult

@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onViewFullHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NailScanSurface)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NailScanLogoCircle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = NailScanAccent,
                    modifier = Modifier.size(26.dp),
                )
            }
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
                )
                Text(
                    text = uiState.userName,
                    style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Box(
            modifier = Modifier
                .size(132.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(NailScanScanButton)
                .clickable(onClick = onScanClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = stringResource(R.string.home_scan_button_description),
                modifier = Modifier.size(52.dp),
                tint = NailScanSurface,
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.home_recent_activity),
            modifier = Modifier.padding(horizontal = 24.dp),
            style = Typography.titleMedium.copy(
                color = NailScanTextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.recentScans.isEmpty()) {
            Text(
                text = stringResource(R.string.home_no_activity),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                uiState.recentScans.forEach { scan ->
                    RecentActivityCard(
                        dateLabel = formatScanDate(scan.createdAt),
                        result = formatScanResult(scan.result),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.home_view_full_history),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onViewFullHistory)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            style = Typography.labelMedium.copy(
                color = NailScanLink,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
