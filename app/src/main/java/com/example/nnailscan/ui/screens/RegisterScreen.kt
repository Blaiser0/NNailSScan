package com.example.nnailscan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.ui.components.NailScanLegalCheckbox
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.components.NailScanTextField
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanLink
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToTerms: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.register_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(12.dp))

        NailScanTextField(
            label = stringResource(R.string.register_full_name_label),
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = stringResource(R.string.register_full_name_placeholder),
        )

        Spacer(modifier = Modifier.height(18.dp))

        NailScanTextField(
            label = stringResource(R.string.register_email_label),
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.register_email_placeholder),
            keyboardType = KeyboardType.Email,
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.password_requirements),
            style = Typography.labelLarge.copy(color = NailScanTextSecondary),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        NailScanTextField(
            label = stringResource(R.string.register_password_label),
            value = password,
            onValueChange = { password = it },
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            keyboardType = KeyboardType.Password,
        )

        Spacer(modifier = Modifier.height(18.dp))

        NailScanTextField(
            label = stringResource(R.string.register_confirm_password_label),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            isPassword = true,
            passwordVisible = confirmPasswordVisible,
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            keyboardType = KeyboardType.Password,
        )

        Spacer(modifier = Modifier.height(16.dp))

        NailScanLegalCheckbox(
            checked = termsAccepted,
            onCheckedChange = { termsAccepted = it },
            modifier = Modifier.clickable(onClick = onNavigateToTerms),
        )

        Spacer(modifier = Modifier.height(24.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.register_button),
            onClick = {
                viewModel.register(
                    fullName = fullName,
                    email = email,
                    password = password,
                    confirmPassword = confirmPassword,
                    termsAccepted = termsAccepted,
                    onSuccess = onRegisterSuccess,
                )
            },
            enabled = !uiState.isLoading,
        )

        Spacer(modifier = Modifier.height(28.dp))

        val loginText = buildAnnotatedString {
            append(stringResource(R.string.register_has_account_prefix))
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NailScanLink)) {
                append(stringResource(R.string.register_login_link))
            }
        }

        Text(
            text = loginText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onNavigateToLogin),
            style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
            textAlign = TextAlign.Center,
        )
    }
}
