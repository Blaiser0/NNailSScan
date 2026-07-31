package com.example.nnailscan.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.data.model.UserRole
import com.example.nnailscan.data.model.AppContent
import com.example.nnailscan.ui.components.ContentTextBlock
import com.example.nnailscan.ui.components.NailScanAuthBrandingDefaults
import com.example.nnailscan.ui.components.NailScanAuthBrandingSection
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanPrimaryDark
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.RoleViewModel

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    roleViewModel: RoleViewModel = viewModel(),
) {
    val context = LocalContext.current
    val roleState by roleViewModel.uiState.collectAsState()
    var showRoleDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = NailScanAuthBrandingDefaults.screenHorizontalPadding,
                vertical = NailScanAuthBrandingDefaults.screenVerticalPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.profile_about_title),
            onBack = onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(NailScanAuthBrandingDefaults.headerToBrandingSpacing))

        NailScanAuthBrandingSection(
            onLogoTripleClick = { showRoleDialog = true },
        )

        AppContent.aboutSections.forEach { section ->
            AboutInfoCard(
                title = section.title,
                body = section.body,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showRoleDialog) {
        val isAdmin = roleState.role == UserRole.ADMIN
        val title = when {
            isAdmin && roleState.isAdminViewMode -> stringResource(R.string.admin_switch_user_title)
            isAdmin -> stringResource(R.string.admin_switch_admin_title)
            roleState.hasPendingAdminRequest -> stringResource(R.string.admin_request_pending_title)
            else -> stringResource(R.string.admin_request_title)
        }
        val message = when {
            isAdmin && roleState.isAdminViewMode -> stringResource(R.string.admin_switch_user_message)
            isAdmin -> stringResource(R.string.admin_switch_admin_message)
            roleState.hasPendingAdminRequest -> stringResource(R.string.admin_request_pending_message)
            else -> stringResource(R.string.admin_request_message)
        }
        val confirmLabel = when {
            isAdmin -> stringResource(R.string.admin_switch_confirm)
            roleState.hasPendingAdminRequest -> stringResource(R.string.admin_dialog_close)
            else -> stringResource(R.string.admin_request_send)
        }

        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRoleDialog = false
                        if (isAdmin) {
                            val enabling = !roleState.isAdminViewMode
                            roleViewModel.toggleAdminViewMode()
                            Toast.makeText(
                                context,
                                if (enabling) {
                                    context.getString(R.string.admin_mode_enabled)
                                } else {
                                    context.getString(R.string.user_mode_enabled)
                                },
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else if (!roleState.hasPendingAdminRequest) {
                            roleViewModel.requestAdminAccess { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                ) {
                    Text(confirmLabel)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoleDialog = false }) {
                    Text(stringResource(R.string.admin_dialog_cancel))
                }
            },
        )
    }
}

@Composable
internal fun AboutInfoCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NailScanSurface, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = title,
            style = Typography.titleMedium.copy(
                color = NailScanPrimaryDark,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        ContentTextBlock(text = body)
    }
}
