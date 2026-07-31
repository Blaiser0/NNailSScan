package com.example.nnailscan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.data.model.AdminRequest
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.AdminRequestsViewModel

@Composable
fun AdminRequestsScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminRequestsViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.admin_requests_title),
            style = Typography.titleMedium.copy(
                color = NailScanTextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.requests.isEmpty()) {
            Text(
                text = stringResource(R.string.admin_requests_empty),
                style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(uiState.requests, key = { it.id }) { request ->
                    AdminRequestCard(
                        request = request,
                        enabled = !uiState.isProcessing,
                        onApprove = { viewModel.approve(request) },
                        onDeny = { viewModel.deny(request) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminRequestCard(
    request: AdminRequest,
    enabled: Boolean,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NailScanSurface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = request.fullName.ifBlank { "Usuario" },
                style = Typography.bodyLarge.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = request.email,
                style = Typography.labelLarge.copy(color = NailScanTextSecondary),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onApprove,
                    enabled = enabled,
                    colors = ButtonDefaults.buttonColors(containerColor = NailScanButton),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.admin_request_approve))
                }
                Button(
                    onClick = onDeny,
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.admin_request_deny))
                }
            }
        }
    }
}
