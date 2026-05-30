package com.example.nnailscan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.navigation.PasswordResetState
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.NailScanTextField
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.ChangePasswordViewModel

@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onPasswordChanged: () -> Unit,
    onVerificationRequired: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!PasswordResetState.isEmailVerified || PasswordResetState.oobCode.isNullOrBlank()) {
            Toast.makeText(
                context,
                context.getString(R.string.change_password_verification_required),
                Toast.LENGTH_LONG,
            ).show()
            onVerificationRequired()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    if (!PasswordResetState.isEmailVerified || PasswordResetState.oobCode.isNullOrBlank()) {
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.change_password_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.change_password_in_app_only),
            style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.password_requirements),
            style = Typography.labelLarge.copy(color = NailScanTextSecondary),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        NailScanTextField(
            label = stringResource(R.string.change_password_new_label),
            value = newPassword,
            onValueChange = { newPassword = it },
            isPassword = true,
            passwordVisible = newPasswordVisible,
            onTogglePasswordVisibility = { newPasswordVisible = !newPasswordVisible },
            keyboardType = KeyboardType.Password,
        )

        Spacer(modifier = Modifier.height(18.dp))

        NailScanTextField(
            label = stringResource(R.string.change_password_confirm_label),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            keyboardType = KeyboardType.Password,
        )

        Spacer(modifier = Modifier.weight(1f))

        NailScanPrimaryButton(
            text = stringResource(R.string.change_password_button),
            onClick = {
                viewModel.changePassword(
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    onSuccess = onPasswordChanged,
                )
            },
            enabled = !uiState.isLoading,
        )
    }
}
