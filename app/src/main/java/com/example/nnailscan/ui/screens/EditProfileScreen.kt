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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.NailScanTextField
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.ProfileViewModel

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var fullName by rememberSaveable(uiState.fullName) { mutableStateOf(uiState.fullName) }

    LaunchedEffect(uiState.fullName) {
        if (fullName.isBlank()) {
            fullName = uiState.fullName
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.edit_profile_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(16.dp))

        NailScanTextField(
            label = stringResource(R.string.profile_name_label),
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = stringResource(R.string.register_full_name_placeholder),
        )

        Spacer(modifier = Modifier.height(18.dp))

        NailScanTextField(
            label = stringResource(R.string.profile_email_label),
            value = uiState.email,
            onValueChange = {},
            enabled = false,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.edit_profile_email_hint),
            style = Typography.labelLarge.copy(color = NailScanTextSecondary),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.edit_profile_save),
            onClick = {
                viewModel.updateProfile(fullName) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.edit_profile_success),
                        Toast.LENGTH_SHORT,
                    ).show()
                    onSaved()
                }
            },
            enabled = !uiState.isSaving && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
