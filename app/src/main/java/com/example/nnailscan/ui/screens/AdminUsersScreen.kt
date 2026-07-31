package com.example.nnailscan.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.data.model.UserProfile
import com.example.nnailscan.data.model.UserRole
import com.example.nnailscan.ui.components.AdminBadge
import com.example.nnailscan.ui.components.ProfileAvatar
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.AdminUsersViewModel

@Composable
fun AdminUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: AdminUsersViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.admin_users_title),
            style = Typography.titleMedium.copy(
                color = NailScanTextPrimary,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.users, key = { it.uid }) { user ->
                AdminUserCard(user = user)
            }
        }
    }
}

@Composable
private fun AdminUserCard(user: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NailScanSurface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(
                photoUrl = user.photoUrl,
                onClick = {},
                enabled = false,
                size = 48.dp,
            )
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.fullName.ifBlank { "Usuario" },
                        style = Typography.bodyLarge.copy(
                            color = NailScanTextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    if (user.role == UserRole.ADMIN) {
                        AdminBadge(modifier = Modifier.padding(start = 6.dp))
                    }
                }
                Text(
                    text = user.email,
                    style = Typography.labelLarge.copy(color = NailScanTextSecondary),
                )
                Text(
                    text = if (user.role == UserRole.ADMIN) {
                        stringResource(R.string.admin_role_label)
                    } else {
                        stringResource(R.string.user_role_label)
                    },
                    style = Typography.labelLarge.copy(color = NailScanTextSecondary),
                )
            }
        }
    }
}
