package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.navigation.ProfileDestination
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.ProfileMenuOptionCard
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanLogoCircle
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigate: (ProfileDestination) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val displayName = uiState.fullName.ifBlank { "Usuario" }
    val displayEmail = uiState.email.ifBlank { "—" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NailScanLogoCircle),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = NailScanTextSecondary,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp),
                ) {
                    Text(
                        text = displayName,
                        style = Typography.bodyLarge.copy(
                            color = NailScanTextPrimary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text = displayEmail,
                        style = Typography.labelLarge.copy(color = NailScanTextSecondary),
                    )
                }

                Button(
                    onClick = { /* TODO: editar perfil */ },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NailScanButton,
                        contentColor = NailScanSurface,
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.profile_edit),
                        style = Typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            ProfileMenuOptionCard(
                title = stringResource(R.string.profile_support_title),
                subtitle = stringResource(R.string.profile_support_subtitle),
                icon = Icons.Outlined.HeadsetMic,
                onClick = { onNavigate(ProfileDestination.TechnicalSupport) },
            )
        }

        item {
            ProfileMenuOptionCard(
                title = stringResource(R.string.profile_feedback_title),
                subtitle = stringResource(R.string.profile_feedback_subtitle),
                icon = Icons.Outlined.StarOutline,
                onClick = { onNavigate(ProfileDestination.Feedback) },
            )
        }

        item {
            ProfileMenuOptionCard(
                title = stringResource(R.string.profile_about_title),
                icon = Icons.Outlined.Info,
                onClick = { onNavigate(ProfileDestination.About) },
            )
        }

        item {
            ProfileMenuOptionCard(
                title = stringResource(R.string.terms_title),
                icon = Icons.Outlined.AssignmentTurnedIn,
                onClick = { onNavigate(ProfileDestination.Terms) },
            )
        }

        item {
            ProfileMenuOptionCard(
                title = stringResource(R.string.terms_privacy_title),
                icon = Icons.Outlined.Shield,
                onClick = { onNavigate(ProfileDestination.Privacy) },
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            NailScanPrimaryButton(
                text = stringResource(R.string.profile_logout),
                onClick = { viewModel.signOut(onLogout) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
